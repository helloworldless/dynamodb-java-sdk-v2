package com.davidagood.awssdkv2.dynamodb;

import com.davidagood.awssdkv2.dynamodb.repository.Repository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class App {

    public static final String CUSTOMER_ID = "123";
    public static final String TABLE_NAME = "java-sdk-v2";
    private static final Logger log = LoggerFactory.getLogger(App.class);
    private final Repository repository;

    public App(Repository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        var dynamoDbClient = DynamoDbClient.builder().build();
        Repository repository = Repository.Factory.create(dynamoDbClient, TABLE_NAME);
        new App(repository).run();
    }

    public void run() {
        // this.repository.deleteAllItems();

        this.populateCustomer();
        this.populateOrders();

        // Retrieve customer and orders
        Customer retrievedCustomer = repository.getCustomerById(CUSTOMER_ID);
        log.info("Found Customer with id={}", retrievedCustomer.getId());

        Map<String, AttributeValue> customerDdbJson = repository.getCustomerByIdDynamoDbJson(CUSTOMER_ID);
        log.info("Customer as DynamoDB JSON: {}", customerDdbJson);

        CustomerWithOrders customerWithOrders = repository.getCustomerAndRecentOrders(CUSTOMER_ID, 1);
        log.info("Result of query item collection: {}", customerWithOrders);
    }

    public void populateCustomer() {
        Customer customer = new Customer(CUSTOMER_ID);
        this.repository.insertCustomer(customer);
    }

    public void populateOrders() {
        this.repository.insertOrder(new Order("2020-11-25", CUSTOMER_ID));
        this.repository.insertOrder(new Order("2020-12-01", CUSTOMER_ID));
        this.repository.insertOrder(new Order("2020-12-06", CUSTOMER_ID));
    }

}
