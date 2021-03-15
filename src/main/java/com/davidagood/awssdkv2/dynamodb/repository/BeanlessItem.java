package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Value
@Builder
public class BeanlessItem implements DynamoDbItem {

    @NonNull
    String id;

    @NonNull
    String message;

    @NonNull
    Instant createdAt;

    @NonNull
    BeanlessStatus status;

    @NonNull
    BeanlessNestedItem nestedItem;

    @NonNull
    BeanlessNestedJson nestedJson;

    public static AttributeValue buildPartitionKey(String id) {
        return AttributeValue.builder().s("BEANLESS#" + id).build();
    }

    public static AttributeValue buildSortKey() {
        return AttributeValue.builder().s("A").build();
    }

    public static BeanlessItem fromMap(Map<String, AttributeValue> item) {
        /*
         * The `Status` attribute is non-nullable at the application layer (this is an immutable value class and
         * `status` is marked `@NonNull`. However, here we demonstrate how to handle the case where perhaps this
         * attribute was not always non-nullable, so there are pre-existing items in the DynamoDB table which
         * do not have this attribute. Also, let's say that we do not wish to run a migration script to
         * scan the table and manually set the default value on every item. Instead, we handle the default
         * value dynamically here in the application layer.
         */
        BeanlessStatus status = Optional.ofNullable(item.get("Status"))
            .map(AttributeValue::s)
            .map(BeanlessStatus::valueOf)
            .orElse(BeanlessStatus.INIT);
        return BeanlessItem.builder()
            .id(item.get("Id").s())
            .message(item.get("Message").s())
            .createdAt(Instant.parse(item.get("CreatedAt").s()))
            .status(status)
            .nestedItem(BeanlessNestedItem.fromMap(item.get("NestedItem").m()))
            .nestedJson(BeanlessNestedJson.fromJson(item.get("NestedJson").s()))
            .build();
    }

    public static Key buildKeyForId(String id) {
        return Key.builder().partitionValue(buildPartitionKey(id)).sortValue(buildSortKey()).build();
    }

    @Override
    public Map<String, AttributeValue> asDynamoDbJson() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("PK", buildPartitionKey(this.id));
        map.put("SK", buildSortKey());
        map.put("Type", AttributeValue.builder().s("BeanlessItem").build());
        map.put("Id", AttributeValue.builder().s(this.id).build());
        map.put("Message", AttributeValue.builder().s(this.message).build());
        map.put("CreatedAt", AttributeValue.builder().s(this.createdAt.toString()).build());
        map.put("Status", AttributeValue.builder().s(this.status.name()).build());
        map.put("NestedItem", AttributeValue.builder().m(this.nestedItem.asDynamoDbJson()).build());
        map.put("NestedJson", this.nestedJson.toJson());
        return map;
    }

}
