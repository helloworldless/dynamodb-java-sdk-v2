package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.mapper.ImmutableAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;
import java.util.function.BiConsumer;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;
import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primarySortKey;

@Value
@Builder
public class StaticSchemaImmutableItem {

    /*
     * Use this when the Enhanced Client forces us to provide a setter even for a derived attribute
     */
    static final BiConsumer<StaticSchemaImmutableItem.StaticSchemaImmutableItemBuilder, String> NO_OP = (__, ___) -> {
    };

    @NonNull
    @Getter(onMethod_ = {@DynamoDbAttribute("Id")})
    String id;
    @NonNull
    @Getter(onMethod_ = {@DynamoDbAttribute("Message")})
    String message;

    static ImmutableAttribute.Builder<StaticSchemaImmutableItem, StaticSchemaImmutableItemBuilder, String> immutableAttribute() {
        return ImmutableAttribute.builder(StaticSchemaImmutableItem.class, StaticSchemaImmutableItemBuilder.class,
            String.class);
    }

    public static TableSchema<StaticSchemaImmutableItem> schema() {
        var pk = immutableAttribute().addTag(primaryPartitionKey()).name("PK").getter(StaticSchemaImmutableItem::getPk).setter(NO_OP).build();
        var sk = immutableAttribute().addTag(primarySortKey()).name("SK").getter(StaticSchemaImmutableItem::getSk).setter(NO_OP).build();
        var id = immutableAttribute().name("Id").getter(StaticSchemaImmutableItem::getId).setter(StaticSchemaImmutableItemBuilder::id).build();
        var type = immutableAttribute().name("Type").getter(StaticSchemaImmutableItem::getType).setter(NO_OP).build();
        var message = immutableAttribute().name("Message").getter(StaticSchemaImmutableItem::getMessage).setter(StaticSchemaImmutableItemBuilder::message).build();
        return TableSchema.builder(StaticSchemaImmutableItem.class, StaticSchemaImmutableItem.StaticSchemaImmutableItemBuilder.class)
            .newItemBuilder(StaticSchemaImmutableItem::builder, StaticSchemaImmutableItem.StaticSchemaImmutableItemBuilder::build)
            .attributes(pk, sk, type, id, message)
            .build();
    }

    static String buildPartitionKey(String id) {
        return "STATIC_SCHEMA_IMMUTABLE_ITEM#" + id;
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
        return "StaticSchemaImmutableItem";
    }

}
