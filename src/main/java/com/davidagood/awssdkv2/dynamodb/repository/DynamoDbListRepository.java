package com.davidagood.awssdkv2.dynamodb.repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DynamoDbListRepository {

    private static final Logger log = LoggerFactory.getLogger(DynamoDbListRepository.class);

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    public DynamoDbListRepository(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
    }

    public void insertItemWithEmptyList() {
        Map<String, AttributeValue> item = new HashMap<>();
        item.put("PK", AttributeValue.builder().s("ITEM#123").build());
        item.put("SK", AttributeValue.builder().s("A").build());
        item.put("List", AttributeValue.builder().l(Collections.emptyList()).build());
        var putItemRequest = PutItemRequest.builder()
            .tableName(tableName)
            .item(item)
            .build();
        try {
            PutItemResponse response = dynamoDbClient.putItem(putItemRequest);
            log.info("response: {}", response);

        } catch (ConditionalCheckFailedException e) {
            log.error("Condition check failed, item didn't exist", e);
        }
    }

    public Map<String, AttributeValue> getItemWithList() {
        GetItemRequest request = GetItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("PK", AttributeValue.builder().s("ITEM#123").build(), "SK", AttributeValue.builder().s("A").build()))
            .build();
        return dynamoDbClient.getItem(request).item();
    }

    public void insertValueAtIndex(int i, AttributeValue value) {
        var updateItemRequest = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("PK", AttributeValue.builder().s("ITEM#123").build(), "SK", AttributeValue.builder().s("A").build()))
            .updateExpression(String.format("SET #list[%s] = :value", i))
            .conditionExpression("attribute_exists(PK)")
            .expressionAttributeNames(Map.of("#list", "List"))
            .expressionAttributeValues(Map.of(":value", value))
            .build();
        try {
            UpdateItemResponse response = dynamoDbClient.updateItem(updateItemRequest);
            log.info("response: {}", response);

        } catch (ConditionalCheckFailedException e) {
            log.error("Condition check failed, item didn't exist", e);
        }
    }

    public void appendValuesToList(AttributeValue... values) {
        var updateItemRequest = UpdateItemRequest.builder()
            .tableName(tableName)
            .key(Map.of("PK", AttributeValue.builder().s("ITEM#123").build(), "SK", AttributeValue.builder().s("A").build()))
            .updateExpression("SET #list = list_append(#list, :values)")
            .conditionExpression("attribute_exists(PK)")
            .expressionAttributeNames(Map.of("#list", "List"))
            .expressionAttributeValues(Map.of(":values", AttributeValue.builder().l(values).build()))
            .build();
        try {
            UpdateItemResponse response = dynamoDbClient.updateItem(updateItemRequest);
            log.info("response: {}", response);

        } catch (ConditionalCheckFailedException e) {
            log.error("Condition check failed, item didn't exist", e);
        }
    }

}
