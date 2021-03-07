package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbImmutable;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

@Value
@Builder
@DynamoDbImmutable(builder = ImmutableBeanItem.ImmutableBeanItemBuilder.class)
public class ImmutableBeanItem {

    String pk; // Derived attribute but required for DynamoDbImmutable
    String sk; // Derived attribute but required for DynamoDbImmutable
    String type; // Derived attribute but required for DynamoDbImmutable

    @NonNull
    @Getter(onMethod_ = {@DynamoDbAttribute("Id")})
    String id;

    @NonNull
    @Getter(onMethod_ = {@DynamoDbAttribute("Message")})
    String message;

    static String buildPartitionKey(String id) {
        return "IMMUTABLE_BEAN_ITEM#" + id;
    }

    static String buildSortKey() {
        return "A";
    }

    public static Key buildKeyForId(String id) {
        return Key.builder().partitionValue(buildPartitionKey(id)).sortValue(buildSortKey()).build();
    }


    public static Map<String, AttributeValue> buildKeyMayForId(String id) {
        return Map.of(
            "PK", AttributeValue.builder().s(buildPartitionKey(id)).build(),
            "SK", AttributeValue.builder().s(buildSortKey()).build());
    }


    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPk() {
        return buildPartitionKey(this.id);
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSk() {
        return buildSortKey();
    }

    @DynamoDbAttribute("Type")
    public String getType() {
        return "ImmutableBeanItem";
    }


}
