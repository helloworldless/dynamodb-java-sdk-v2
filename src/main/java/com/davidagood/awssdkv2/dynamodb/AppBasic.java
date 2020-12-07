package com.davidagood.awssdkv2.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class AppBasic {
    public static void main(String[] args) {
        DynamoDbClient dynamoDbClient = DynamoDbClient.builder()
                .region(Region.US_EAST_1)
                .build();
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();
        TableSchema<Customer> customerTableSchema = TableSchema.fromClass(Customer.class);
        DynamoDbTable<Customer> customerTable = dynamoDbEnhancedClient.table("java-sdk-v2", customerTableSchema);

        Customer customer = new Customer();
        customer.setId("123");
        customerTable.putItem(customer);

        Customer retrievedCustomer = customerTable.getItem(customer);
    }
}
