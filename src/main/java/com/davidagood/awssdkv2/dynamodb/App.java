package com.davidagood.awssdkv2.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.PutItemEnhancedRequest;
import software.amazon.awssdk.regions.Region;
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

import java.util.List;
import java.util.Map;

import static java.util.Objects.isNull;

public class App {

    private static final String TABLE_NAME = "java-sdk-v2";
    private static final String CUSTOMER_ID = "123";

    private final DynamoDbClient dynamoDbClient;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private final TableSchema<Customer> customerTableSchema;
    private final DynamoDbTable<Customer> customerTable;

    private final TableSchema<Order> orderTableSchema;
    private final DynamoDbTable<Order> orderTable;

    public App() {
        this.dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        this.dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        this.customerTableSchema = TableSchema.fromClass(Customer.class);
        this.customerTable = dynamoDbEnhancedClient.table(TABLE_NAME, customerTableSchema);

        this.orderTableSchema = TableSchema.fromClass(Order.class);
        this.orderTable = dynamoDbEnhancedClient.table(TABLE_NAME, orderTableSchema);
    }

    public static void main(String[] args) {
        App app = new App();

        app.populateCustomer();
        app.populateOrders();

        // app.deleteAllItems();

        Customer retrievedCustomer = app.getCustomerById(CUSTOMER_ID);
        System.out.println("Found Customer with id=" + retrievedCustomer.getId());

        Map<String, AttributeValue> customerDdbJson = app.getCustomerByIdDynamoDbJson(CUSTOMER_ID);
        System.out.println("Customer as DynamoDB JSON:" + customerDdbJson);

        CustomerItemCollection customerItemCollection = app.getCustomerAndRecentOrders(CUSTOMER_ID, 1);
        System.out.println("Result of query item collection: " + customerItemCollection);
    }

    void populateCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        insertCustomer(customer);
    }

    void populateOrders() {
        Order order = new Order();
        order.setId("2020-11-25");
        order.setCustomerId(CUSTOMER_ID);

        Order order2 = new Order();
        order2.setId("2020-12-01");
        order2.setCustomerId(CUSTOMER_ID);

        Order order3 = new Order();
        order3.setId("2020-12-06");
        order3.setCustomerId(CUSTOMER_ID);

        insertOrder(order);
        insertOrder(order2);
        insertOrder(order3);
    }

    CustomerItemCollection getCustomerAndRecentOrders(String customerId,
                                                      int newestOrdersCount) {
        AttributeValue customerPk = AttributeValue.builder().s(Customer.prefixedId(customerId)).build();
        var queryRequest = QueryRequest.builder()
                .tableName(TABLE_NAME)
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

        var customerItemCollection = new CustomerItemCollection();

        for (Map<String, AttributeValue> item : items) {

            // Every item must have a 'Type' Attribute
            AttributeValue type = item.get("Type");

            if (isNull(type) || isNull(type.s()) || type.s().isEmpty()) {
                throw new DynamoDbInvalidEntityException("Required attribute 'Type' is missing or empty on Item with attributes: " + item);
            }

            // Switch on the 'Type' and use the respective TableSchema
            // to marshall the DynamoDB JSON into the value class
            switch (type.s()) {
                case Customer.CUSTOMER_TYPE:
                    Customer customer =
                            customerTableSchema.mapToItem(item);
                    customerItemCollection.setCustomer(customer);
                    break;
                case Order.ORDER_TYPE:
                    Order order =
                            orderTableSchema.mapToItem(item);
                    customerItemCollection.addOrder(order);
                    break;
                default:
                    throw new DynamoDbInvalidEntityException(String.format("Found unhandled Type=%s on Item with attributes: %s", type.s(), item));
            }

        }

        return customerItemCollection;
    }

    void insertCustomer(Customer customer) {
        System.out.printf("Inserting customer: %s%n", customer);
        this.customerTable.putItem(customer);
    }

    void insertCustomerDoNotOverwrite(Customer customer) {
        var expression = Expression.builder()
                .expression("attribute_not_exists(PK)")
                .build();
        var putItemEnhancedRequest = PutItemEnhancedRequest.builder(Customer.class)
                .item(customer)
                .conditionExpression(expression)
                .build();
        try {
            this.customerTable.putItem(putItemEnhancedRequest);
        } catch (ConditionalCheckFailedException e) {
            throw new DynamoDbEntityAlreadyExistsException("Attempted to overwrite an item which already exists with PK=" + customer.getPartitionKey());
        }
    }

    void insertOrder(Order order) {
        System.out.printf("Inserting order: %s%n", order);
        this.orderTable.putItem(order);
    }

    Customer getCustomerById(String id) {
        var key = Key.builder().partitionValue(Customer.prefixedId(id)).sortValue(Customer.A_RECORD).build();
        return this.customerTable.getItem(key);
    }

    Map<String, AttributeValue> getCustomerByIdDynamoDbJson(String id) {
        var pk = AttributeValue.builder().s(Customer.prefixedId(id)).build();
        var sk = AttributeValue.builder().s(Customer.A_RECORD).build();
        var getItemRequest = GetItemRequest.builder()
                .tableName("java-sdk-v2")
                .key(Map.of("PK", pk, "SK", sk))
                .build();
        GetItemResponse item = dynamoDbClient.getItem(getItemRequest);
        return item.item();
    }

    void deleteAllItems() {
        var scanRequest = ScanRequest.builder()
                .tableName(TABLE_NAME)
                .attributesToGet("PK", "SK")
                .build();
        // Note: Normally, full-table scans should be avoided in DynamoDB
        ScanResponse scanResponse = dynamoDbClient.scan(scanRequest);
        for (Map<String, AttributeValue> item : scanResponse.items()) {
            var deleteItemRequest = DeleteItemRequest.builder()
                    .tableName(TABLE_NAME)
                    .key(Map.of("PK", item.get("PK"), "SK", item.get("SK")))
                    .build();
            DeleteItemResponse deleteItemResponse = dynamoDbClient.deleteItem(deleteItemRequest);
            System.out.println("DeleteItemResponse: " + deleteItemResponse.toString());
        }
    }

}
