package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.Customer;
import com.davidagood.awssdkv2.dynamodb.CustomerWithOrders;
import com.davidagood.awssdkv2.dynamodb.DynamoDbEntityAlreadyExistsException;
import com.davidagood.awssdkv2.dynamodb.DynamoDbInvalidEntityException;
import com.davidagood.awssdkv2.dynamodb.Order;
import com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ConditionalCheckFailedException;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemResponse;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

class DynamoDbRepository implements Repository {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;
    private final DynamoDbItemMapper mapper = DynamoDbItemMapper.INSTANCE;

    private final TableSchema<CustomerItem> customerTableSchema;

    @Override
    @VisibleForTesting
    public DynamoDbTable<CustomerItem> getCustomerTable() {
        return customerTable;
    }

    private final DynamoDbTable<CustomerItem> customerTable;

    private final TableSchema<OrderItem> orderTableSchema;
    private final DynamoDbTable<OrderItem> orderTable;

    DynamoDbRepository(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        this.customerTableSchema = TableSchema.fromClass(CustomerItem.class);
        this.customerTable = dynamoDbEnhancedClient.table(tableName, customerTableSchema);

        this.orderTableSchema = TableSchema.fromClass(OrderItem.class);
        this.orderTable = dynamoDbEnhancedClient.table(tableName, orderTableSchema);
    }

    @Override
    public CustomerWithOrders getCustomerAndRecentOrders(String customerId,
                                                         int newestOrdersCount) {
        AttributeValue customerPk = AttributeValue.builder().s(CustomerItem.prefixedId(customerId)).build();
        var queryRequest = QueryRequest.builder()
                .tableName(tableName)
                // Define aliases for the Attribute, '#pk' and the value, ':pk'
                .keyConditionExpression("#pk = :pk")
                // '#pk' refers to the Attribute 'PK'
                .expressionAttributeNames(Map.of("#pk", "PK"))
                // ':pk' refers to the customer PK of interest
                .expressionAttributeValues(Map.of(":pk", customerPk))
                // Search from "bottom to top"
                .scanIndexForward(false)
                // One customer, plus N newest orders
                .limit(1 + newestOrdersCount)
                .build();

        // Use the DynamoDbClient directly rather than the
        // DynamoDbEnhancedClient or DynamoDbTable
        QueryResponse queryResponse = dynamoDbClient.query(queryRequest);

        // The result is a list of items in DynamoDB JSON format
        List<Map<String, AttributeValue>> items = queryResponse.items();

        Customer customer = null;
        List<Order> orders = new ArrayList<>();

        for (Map<String, AttributeValue> item : items) {

            // Every item must have a 'Type' Attribute
            AttributeValue type = item.get("Type");

            if (isNull(type) || isNull(type.s()) || type.s().isEmpty()) {
                throw new DynamoDbInvalidEntityException("Required attribute 'Type' is missing or empty on Item with attributes: " + item);
            }

            // Switch on the 'Type' and use the respective TableSchema
            // to marshall the DynamoDB JSON into the value class
            switch (type.s()) {
                case CustomerItem.CUSTOMER_TYPE:
                    CustomerItem customerItem =
                            customerTableSchema.mapToItem(item);
                    customer = mapper.mapFromItem(customerItem);
                    break;
                case OrderItem.ORDER_TYPE:
                    OrderItem orderItem = orderTableSchema.mapToItem(item);
                    orders.add(mapper.mapFromItem(orderItem));
                    break;
                default:
                    throw new DynamoDbInvalidEntityException(String.format("Found unhandled Type=%s on Item with attributes: %s", type.s(), item));
            }

        }

        return new CustomerWithOrders(customer, orders);
    }

    @Override
    public void insertCustomer(Customer customer) {
        log.info("Inserting customer: {}}", customer);
        this.customerTable.putItem(mapper.mapToItem(customer));
    }

    @Override
    public void insertCustomerDoNotOverwrite(Customer customer) {
        CustomerItem customerItem = mapper.mapToItem(customer);
        var expression = Expression.builder()
                .expression("attribute_not_exists(PK)")
                .build();
        var putItemEnhancedRequest = PutItemEnhancedRequest.builder(CustomerItem.class)
                .item(customerItem)
                .conditionExpression(expression)
                .build();
        try {
            this.customerTable.putItem(putItemEnhancedRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new DynamoDbEntityAlreadyExistsException("Attempted to overwrite an item which already exists with PK=" + customerItem.getPartitionKey());
        }
    }

    @Override
    public void insertOrder(Order order) {
        log.info("Inserting order: {}", order);
        this.orderTable.putItem(mapper.mapToItem(order));
    }

    @Override
    public Customer getCustomerById(String id) {
        var key = Key.builder().partitionValue(CustomerItem.prefixedId(id)).sortValue(CustomerItem.A_RECORD).build();
        return mapper.mapFromItem(this.customerTable.getItem(key));
    }

    @Override
    public Map<String, AttributeValue> getCustomerByIdDynamoDbJson(String id) {
        var pk = AttributeValue.builder().s(CustomerItem.prefixedId(id)).build();
        var sk = AttributeValue.builder().s(CustomerItem.A_RECORD).build();
        var getItemRequest = GetItemRequest.builder()
                .tableName("java-sdk-v2")
                .key(Map.of("PK", pk, "SK", sk))
                .build();
        GetItemResponse item = dynamoDbClient.getItem(getItemRequest);
        return item.item();
    }

    @Override
    public void deleteAllItems() {
        var scanRequest = ScanRequest.builder()
                .tableName(tableName)
                .attributesToGet("PK", "SK")
                .build();
        // Note: Normally, full-table scans should be avoided in DynamoDB
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : scanResponse.items()) {
            var deleteItemRequest = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(Map.of("PK", item.get("PK"), "SK", item.get("SK")))
                    .build();
            DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);
            log.info("DeleteItemResponse: {}", deleteItemResponse);
        }
    }

}