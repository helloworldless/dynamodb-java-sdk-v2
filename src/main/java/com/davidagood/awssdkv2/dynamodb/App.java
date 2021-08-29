package com.davidagood.awssdkv2.dynamodb;

import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.model.Order;
import com.davidagood.awssdkv2.dynamodb.repository.Repository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.net.URI;

public class App {

    public static final String CUSTOMER_ID = "123";

    public static final String TABLE_NAME = "java-sdk-v2";

    public static final ObjectMapper MAPPER = new ObjectMapper()
        .registerModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    private static final Logger log = LoggerFactory.getLogger(App.class);

    private final Repository repository;

    public App(Repository repository) {
        this.repository = repository;
    }

    public static void main(String[] args) {
        DynamoDbClient dynamoDbClient = buildDynamoDbClient();
        Repository repository = Repository.Factory.create(dynamoDbClient, TABLE_NAME, MAPPER);
        new App(repository).run();
    }

    static DynamoDbClient buildDynamoDbClient() {
        String dynamoDBLocalUrl = System.getenv("DYNAMODB_LOCAL_URL");
        return Strings.isNullOrEmpty(dynamoDBLocalUrl)
            ? DynamoDbClient.create()
            : DynamoDbClient.builder().endpointOverride(URI.create(dynamoDBLocalUrl)).build();
    }

    public void run() {
        this.repository.createTableIfNotExists();

        // Start the table with a clean slate to avoid conflicts
        this.repository.deleteAllItems();

        this.populateCustomer();
        this.populateOrders();

        // Retrieve customer and orders
        Customer retrievedCustomer = repository.getCustomerById(CUSTOMER_ID);
        log.info("Found Customer with id={}", retrievedCustomer.getId());

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
