package com.davidagood.awssdkv2.dynamodb;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import static com.davidagood.awssdkv2.dynamodb.App.CUSTOMER_ID;
import static com.davidagood.awssdkv2.dynamodb.App.TABLE_NAME;
import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbIntegrationTest extends LocalDynamoDbSyncTestBase {

    Repository repository = new Repository(getDynamoDbClient(), getConcreteTableName(TABLE_NAME));

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
        Customer customer = new Customer();
        customer.setId(customerId);
        repository.insertCustomer(customer);
        assertThat(repository.getCustomerById(customerId)).isEqualTo(customer);
    }

}
