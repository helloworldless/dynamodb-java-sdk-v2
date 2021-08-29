package com.davidagood.awssdkv2.dynamodb.repository;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.CreateTableRequest;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DynamoDbListRepositoryIT extends LocalDynamoDbSyncTestBase {

    static final String TABLE_NAME = "DynamoDbListRepositoryTestTable";

    DynamoDbListRepository repository = new DynamoDbListRepository(getDynamoDbClient(), getConcreteTableName(TABLE_NAME));

    @BeforeEach
    void createTable() {
        CreateTableRequest request = DynamoDbRepository.createTableRequest(getConcreteTableName(TABLE_NAME));
        getDynamoDbClient().createTable(request);
    }

    @AfterEach
    void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
            .tableName(getConcreteTableName(TABLE_NAME))
            .build());
    }

    @Test
    void insertItemWithList() {
        repository.insertItemWithEmptyList();
    }

    @Test
    void addItem() {
        // Empty list
        repository.insertItemWithEmptyList();
        assertThat(repository.getItemWithList().get("List")).isEqualTo(AttributeValue.builder().l(Collections.emptyList()).build());

        // Insert a value at index=0
        var firstValue = AttributeValue.builder().s("first").build();
        repository.insertValueAtIndex(0, firstValue);
        assertThat(repository.getItemWithList().get("List")).isEqualTo(AttributeValue.builder().l(firstValue).build());

        // Insert a value at index=0, replacing the existing value
        var secondValue = AttributeValue.builder().s("second").build();
        repository.insertValueAtIndex(0, secondValue);
        assertThat(repository.getItemWithList().get("List")).isEqualTo(AttributeValue.builder().l(secondValue).build());

        // Insert a value at index=100
        // If the index doesn't exist, the value is inserted at the end of the list
        // In this case index=100 doesn't exist, so the value is inserted at
        // the end of the list at index=1
        var thirdValue = AttributeValue.builder().s("third").build();
        repository.insertValueAtIndex(100, thirdValue);
        assertThat(repository.getItemWithList().get("List"))
            .isEqualTo(AttributeValue.builder().l(secondValue, thirdValue).build());

        // Append values
        var message = AttributeValue.builder().b(SdkBytes.fromByteArray("encoded message".getBytes())).build();
        var affirmative = AttributeValue.builder().bool(true).build();
        repository.appendValuesToList(message, affirmative);
        assertThat(repository.getItemWithList().get("List"))
            .isEqualTo(AttributeValue.builder().l(secondValue, thirdValue, message, affirmative).build());
    }

    @Test
    void heterogeneousDataTypes() {
        // Lists are untyped, i.e. the can contain heterogeneous data types
        var string = AttributeValue.builder().s("apple").build();
        var number = AttributeValue.builder().n("123").build();
        var list = AttributeValue.builder().l(AttributeValue.builder().s("nested list").build()).build();
        var valueMap = Map.of(
            "state", AttributeValue.builder().s("Alabama").build(),
            "abbreviation", AttributeValue.builder().s("AL").build());
        var map = AttributeValue.builder().m(valueMap).build();
        repository.insertItemWithEmptyList();
        repository.appendValuesToList(string, number, list, map);
        assertThat(repository.getItemWithList().get("List"))
            .isEqualTo(AttributeValue.builder().l(string, number, list, map).build());
    }

}