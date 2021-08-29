package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.CustomerWithOrders;
import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.model.Order;
import com.davidagood.awssdkv2.dynamodb.model.Photo;
import com.fasterxml.jackson.databind.ObjectMapper;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.util.Optional;

public interface Repository {

    class Factory {
        public static Repository create(DynamoDbClient dynamoDbClient, String tableName, ObjectMapper objectMapper) {
            return new DynamoDbRepository(dynamoDbClient, tableName, objectMapper);
        }
    }

    CustomerWithOrders getCustomerAndRecentOrders(String customerId,
                                                  int newestOrdersCount);

    void insertCustomer(Customer customer);

    void insertOrder(Order order);

    Customer getCustomerById(String id);

    void insertPhoto(Photo photo);

    Optional<Photo> findPhoto(String photoId);

    void deleteAllItems();

    void createTableIfNotExists();
}
