package com.davidagood.awssdkv2.dynamodb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public class App {

    private static final Logger log = LoggerFactory.getLogger(App.class);

    public static final String CUSTOMER_ID = "123";
    public static final String TABLE_NAME = "java-sdk-v2";

    private final Repository repository;

    public App(Repository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        var dynamoDbClient = DynamoDbClient.builder().build();
        Repository repository = new Repository(dynamoDbClient, TABLE_NAME);
        new App(repository).run();
    }

    public void run() {
        // this.repository.deleteAllItems();

        // this.populateCustomer();
        // this.populateOrders();

        // Retrieve customer and orders
        Customer retrievedCustomer = repository.getCustomerById(CUSTOMER_ID);
        log.info("Found Customer with id={}", retrievedCustomer.getId());

        Map<String, AttributeValue> customerDdbJson = repository.getCustomerByIdDynamoDbJson(CUSTOMER_ID);
        log.info("Customer as DynamoDB JSON: {}", customerDdbJson);

        CustomerItemCollection customerItemCollection = repository.getCustomerAndRecentOrders(CUSTOMER_ID, 1);
        log.info("Result of query item collection: {}", customerItemCollection);
    }

    public void populateCustomer() {
        Customer customer = new Customer();
        customer.setId(CUSTOMER_ID);
        this.repository.insertCustomer(customer);
    }

    public void populateOrders() {
        Order order = new Order();
        order.setId("2020-11-25");
        order.setCustomerId(CUSTOMER_ID);

        Order order2 = new Order();
        order2.setId("2020-12-01");
        order2.setCustomerId(CUSTOMER_ID);

        Order order3 = new Order();
        order3.setId("2020-12-06");
        order3.setCustomerId(CUSTOMER_ID);

        this.repository.insertOrder(order);
        this.repository.insertOrder(order2);
        this.repository.insertOrder(order3);
    }

}
