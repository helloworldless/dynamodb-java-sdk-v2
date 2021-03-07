package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

public class DynamoDbBeanlessRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;
    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    public DynamoDbBeanlessRepository(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
    }

    public static Map<String, AttributeValue> keyAsMap(Key key) {
        return Map.of(
            "PK", key.partitionKeyValue(),
            "SK", key.sortKeyValue().get()
        );
    }

    public void insertItem(DynamoDbItem item) {
        Map<String, AttributeValue> attributes = item.asDynamoDbJson();
        var request = PutItemRequest.builder().tableName(tableName).item(attributes).build();
        dynamoDbClient.putItem(request);
    }

    public BeanlessItem getBeanlessItem(String id) {
        Map<String, AttributeValue> item = this.getBeanlessItemRaw(id);
        return BeanlessItem.fromMap(item);
    }

    public Map<String, AttributeValue> getBeanlessItemRaw(String id) {
        Key key = BeanlessItem.buildKeyForId(id);
        Map<String, AttributeValue> keyMap = keyAsMap(key);
        GetItemRequest request = GetItemRequest.builder().tableName(tableName).key(keyMap).build();
        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.item();
    }

}
