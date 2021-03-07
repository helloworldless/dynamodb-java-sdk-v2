package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class BeanlessItem implements DynamoDbItem {

    @NonNull
    String id;

    @NonNull
    String message;

    @NonNull
    BeanlessNestedItem nestedItem;

    public static AttributeValue buildPartitionKey(String id) {
        return AttributeValue.builder().s("BEANLESS#" + id).build();
    }

    public static AttributeValue buildSortKey() {
        return AttributeValue.builder().s("A").build();
    }

    public static BeanlessItem fromMap(Map<String, AttributeValue> item) {
        return BeanlessItem.builder()
            .id(item.get("Id").s())
            .message(item.get("Message").s())
            .nestedItem(BeanlessNestedItem.fromMap(item.get("NestedItem").m()))
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
        map.put("NestedItem", AttributeValue.builder().m(this.nestedItem.asDynamoDbJson()).build());
        return map;
    }

}
