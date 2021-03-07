package com.davidagood.awssdkv2.dynamodb.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbBeanlessRepositoryTest extends LocalDynamoDbSyncTestBase {

    static final String TABLE_NAME = "DynamoDbBeanlessRepositoryTestTable";

    DynamoDbBeanlessRepository repository = new DynamoDbBeanlessRepository(getDynamoDbClient(), getConcreteTableName(TABLE_NAME));

    @BeforeEach
    void createTable() {
        var pk = KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build();
        var pkDef = AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build();
        var sk = KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build();
        var skDef = AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build();
        CreateTableRequest request = CreateTableRequest.builder()
            .tableName(getConcreteTableName(TABLE_NAME))
            .keySchema(pk, sk)
            .attributeDefinitions(pkDef, skDef)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build();
        getDynamoDbClient().createTable(request);
    }

    @AfterEach
    void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
            .tableName(getConcreteTableName(TABLE_NAME))
            .build());
    }

    @Test
    void beanlessItemRoundTrip() {
        var id = "123";
        var nestedItem = BeanlessNestedItem.builder().name("Jill").phoneNumber("555-555-5555").build();
        var item = BeanlessItem.builder().id(id).message("round trip test").nestedItem(nestedItem).build();
        repository.insertItem(item);
        BeanlessItem result = repository.getBeanlessItem(id);
        assertThat(result).isEqualTo(item);
    }

    @Test
    void beanlessItemRoundTripRaw() {
        var id = "123";
        var message = "round trip raw test";
        var name = "Jill";
        var phoneNumber = "555-555-5555";
        var nestedItem = BeanlessNestedItem.builder().name(name).phoneNumber(phoneNumber).build();
        var item = BeanlessItem.builder().id(id).message(message).nestedItem(nestedItem).build();
        repository.insertItem(item);

        Map<String, AttributeValue> expected = new HashMap<>();
        expected.put("PK", BeanlessItem.buildPartitionKey(id));
        expected.put("SK", BeanlessItem.buildSortKey());
        expected.put("Id", AttributeValue.builder().s(id).build());
        expected.put("Type", AttributeValue.builder().s("BeanlessItem").build());
        expected.put("Message", AttributeValue.builder().s(message).build());

        Map<String, AttributeValue> expectedNested = new HashMap<>();
        expectedNested.put("Name", AttributeValue.builder().s(name).build());
        expectedNested.put("PhoneNumber", AttributeValue.builder().s(phoneNumber).build());

        expected.put("NestedItem", AttributeValue.builder().m(expectedNested).build());

        assertThat(repository.getBeanlessItemRaw(id)).isEqualTo(expected);
    }

}
