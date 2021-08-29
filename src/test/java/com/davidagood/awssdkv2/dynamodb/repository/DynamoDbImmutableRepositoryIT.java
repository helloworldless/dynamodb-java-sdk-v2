package com.davidagood.awssdkv2.dynamodb.repository;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbImmutableRepositoryIT extends LocalDynamoDbSyncTestBase {

    static final String TABLE_NAME = "DynamoDbImmutableBeanTestTable";

    DynamoDbImmutableRepository repository = new DynamoDbImmutableRepository(getDynamoDbClient(), getConcreteTableName(TABLE_NAME));

    @BeforeEach
    void createTable() {
        repository.getImmutableBeanItemTable().createTable();
    }

    @AfterEach
    void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
            .tableName(getConcreteTableName(TABLE_NAME))
            .build());
    }

    @Test
    void immutableBeanItemRoundTrip() {
        var id = "123";
        var item = ImmutableBeanItem.builder().id(id).message("round trip test").build();
        repository.insertImmutableBeanItem(item);
        ImmutableBeanItem result = repository.getImmutableBeanItem(id);
        assertThat(result).isEqualTo(item);
    }

    @Test
    void immutableBeanItemRoundTripRaw() {
        var id = "123";
        var message = "round trip raw test";
        var item = ImmutableBeanItem.builder().id(id).message(message).build();
        repository.insertImmutableBeanItem(item);

        Map<String, AttributeValue> expected = new HashMap<>();
        expected.put("PK", AttributeValue.builder().s(ImmutableBeanItem.buildPartitionKey(id)).build());
        expected.put("SK", AttributeValue.builder().s(ImmutableBeanItem.buildSortKey()).build());
        expected.put("Id", AttributeValue.builder().s(id).build());
        expected.put("Type", AttributeValue.builder().s("ImmutableBeanItem").build());
        expected.put("Message", AttributeValue.builder().s(message).build());

        assertThat(repository.getImmutableBeanItemRaw(id)).isEqualTo(expected);
    }

    @Test
    void staticSchemaImmutableItemRoundTrip() {
        var id = "123";
        var item = StaticSchemaImmutableItem.builder().id(id).message("round trip test").build();
        repository.insertStaticSchemaImmutableItem(item);
        StaticSchemaImmutableItem result = repository.getStaticSchemaImmutableItem(id);
        assertThat(result).isEqualTo(item);
    }

    @Test
    void staticSchemaImmutableItemRoundTripRaw() {
        var id = "123";
        var message = "round trip raw test";
        var item = StaticSchemaImmutableItem.builder().id(id).message(message).build();
        repository.insertStaticSchemaImmutableItem(item);

        Map<String, AttributeValue> expected = new HashMap<>();
        expected.put("PK", AttributeValue.builder().s(StaticSchemaImmutableItem.buildPartitionKey(id)).build());
        expected.put("SK", AttributeValue.builder().s(StaticSchemaImmutableItem.buildSortKey()).build());
        expected.put("Id", AttributeValue.builder().s(id).build());
        expected.put("Type", AttributeValue.builder().s("StaticSchemaImmutableItem").build());
        expected.put("Message", AttributeValue.builder().s(message).build());

        assertThat(repository.getStaticSchemaImmutableItemRaw(id)).isEqualTo(expected);
    }

}
