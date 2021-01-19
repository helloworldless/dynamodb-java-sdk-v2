package com.davidagood.awssdkv2.dynamodb.repository;


import com.davidagood.awssdkv2.dynamodb.CustomerWithOrders;
import com.davidagood.awssdkv2.dynamodb.LocalDynamoDbSyncTestBase;
import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.model.Order;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import java.util.List;

import static com.davidagood.awssdkv2.dynamodb.App.CUSTOMER_ID;
import static com.davidagood.awssdkv2.dynamodb.App.TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DynamoDbIntegrationTest extends LocalDynamoDbSyncTestBase {

    Repository repository = new DynamoDbRepository(getDynamoDbClient(), getConcreteTableName(TABLE_NAME));

    @BeforeEach
    void createTable() {
        repository.getCustomerTable().createTable(r -> r.provisionedThroughput(getDefaultProvisionedThroughput()));
    }

    @AfterEach
    void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
            .tableName(getConcreteTableName(TABLE_NAME))
            .build());
    }

    @Test
    void insertAndRetrieveCustomer() {
        var customerId = CUSTOMER_ID;
        Customer customer = new Customer(customerId);
        repository.insertCustomer(customer);
        assertThat(repository.getCustomerById(customerId)).isEqualTo(customer);
    }

    @Test
    void insertCustomerAlreadyExistsShouldThrow() {
        Customer customer = new Customer(CUSTOMER_ID);
        repository.insertCustomer(customer);
        assertThatThrownBy(() -> repository.insertCustomer(customer))
            .isInstanceOf(DynamoDbEntityAlreadyExistsException.class)
            .hasMessageContaining("already exists");
    }

    @Test
    void itemCollection() {
        String customerId = CUSTOMER_ID;
        Customer customer = new Customer(customerId);
        repository.insertCustomer(customer);

        Order order = new Order("2020-11-25", CUSTOMER_ID);
        Order order2 = new Order("2020-12-06", CUSTOMER_ID);
        Order order3 = new Order("2020-12-01", CUSTOMER_ID);

        repository.insertOrder(order);
        repository.insertOrder(order2);
        repository.insertOrder(order3);

        assertThat(repository.getCustomerAndRecentOrders(customerId, 1))
            .isEqualTo(new CustomerWithOrders(customer, List.of(order2)));
    }

}
