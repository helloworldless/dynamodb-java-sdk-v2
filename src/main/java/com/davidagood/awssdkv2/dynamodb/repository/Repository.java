package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.CustomerWithOrders;
import com.davidagood.awssdkv2.dynamodb.model.Order;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface Repository {

    class Factory {
        public static Repository create(DynamoDbClient dynamoDbClient, String tableName) {
            return new DynamoDbRepository(dynamoDbClient, tableName);
        }
    }

    DynamoDbTable<CustomerItem> getCustomerTable();

    CustomerWithOrders getCustomerAndRecentOrders(String customerId,
                                                  int newestOrdersCount);

    void insertCustomer(Customer customer);

    void insertOrder(Order order);

    Customer getCustomerById(String id);

    Map<String, AttributeValue> getCustomerByIdDynamoDbJson(String id);

    void deleteAllItems();
}
