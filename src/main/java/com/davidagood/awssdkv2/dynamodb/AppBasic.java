package com.davidagood.awssdkv2.dynamodb;

import com.davidagood.awssdkv2.dynamodb.repository.CustomerItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static com.davidagood.awssdkv2.dynamodb.App.TABLE_NAME;


public class AppBasic {

    private static final Logger log = LoggerFactory.getLogger(AppBasic.class);

    public static void main(String[] args) {

        DynamoDbClient dynamoDbClient = App.buildDynamoDbClient();
        DynamoDbEnhancedClient dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        TableSchema<CustomerItem> customerTableSchema = TableSchema.fromClass(CustomerItem.class);
        DynamoDbTable<CustomerItem> customerTable = dynamoDbEnhancedClient.table(TABLE_NAME, customerTableSchema);

        CustomerItem customer = new CustomerItem();
        customer.setId("123");
        customerTable.putItem(customer);

        CustomerItem retrievedCustomer = customerTable.getItem(customer);
        log.info("Found customer item: {}", retrievedCustomer);
    }
}
