package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.GetItemRequest;
import software.amazon.awssdk.services.dynamodb.model.GetItemResponse;

import java.util.Map;

public class DynamoDbImmutableRepository {

    private final DynamoDbClient dynamoDbClient;
    private final String tableName;

    private final DynamoDbEnhancedClient dynamoDbEnhancedClient;

    private final TableSchema<ImmutableBeanItem> immutableBeanItemTableSchema;
    private final DynamoDbTable<ImmutableBeanItem> immutableBeanItemTable;

    private final TableSchema<StaticSchemaImmutableItem> staticSchemaImmutableItemTableSchema;
    private final DynamoDbTable<StaticSchemaImmutableItem> staticSchemaImmutableItemTable;

    public DynamoDbImmutableRepository(DynamoDbClient dynamoDbClient, String tableName) {
        this.dynamoDbClient = dynamoDbClient;
        this.tableName = tableName;
        this.dynamoDbEnhancedClient = DynamoDbEnhancedClient.builder().dynamoDbClient(dynamoDbClient).build();
        this.immutableBeanItemTableSchema = TableSchema.fromClass(ImmutableBeanItem.class);
        this.immutableBeanItemTable = dynamoDbEnhancedClient.table(tableName, immutableBeanItemTableSchema);
        this.staticSchemaImmutableItemTableSchema = StaticSchemaImmutableItem.schema();
        this.staticSchemaImmutableItemTable = dynamoDbEnhancedClient.table(tableName, staticSchemaImmutableItemTableSchema);
    }

    DynamoDbTable<ImmutableBeanItem> getImmutableBeanItemTable() {
        return this.immutableBeanItemTable;
    }

    public void insertImmutableBeanItem(ImmutableBeanItem item) {
        immutableBeanItemTable.putItem(item);
    }

    public ImmutableBeanItem getImmutableBeanItem(String id) {
        Key key = ImmutableBeanItem.buildKeyForId(id);
        return immutableBeanItemTable.getItem(key);
    }

    public Map<String, AttributeValue> getImmutableBeanItemRaw(String id) {
        var request = GetItemRequest.builder().tableName(tableName).key(ImmutableBeanItem.buildKeyMayForId(id)).build();
        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.item();
    }

    public void insertStaticSchemaImmutableItem(StaticSchemaImmutableItem item) {
        staticSchemaImmutableItemTable.putItem(item);
    }

    public StaticSchemaImmutableItem getStaticSchemaImmutableItem(String id) {
        Key key = StaticSchemaImmutableItem.buildKeyForId(id);
        return staticSchemaImmutableItemTable.getItem(key);
    }

    public Map<String, AttributeValue> getStaticSchemaImmutableItemRaw(String id) {
        var request = GetItemRequest.builder()
            .tableName(tableName)
            .key(StaticSchemaImmutableItem.buildKeyMayForId(id))
            .build();
        GetItemResponse response = dynamoDbClient.getItem(request);
        return response.item();
    }

}
