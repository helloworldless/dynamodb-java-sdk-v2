package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.HashMap;
import java.util.Map;

@Value
@Builder
public class BeanlessNestedItem implements DynamoDbItem {

    @NonNull
    String name;

    @NonNull
    String phoneNumber;

    public static BeanlessNestedItem fromMap(Map<String, AttributeValue> item) {
        return BeanlessNestedItem.builder()
            .name(item.get("Name").s())
            .phoneNumber(item.get("PhoneNumber").s())
            .build();
    }

    @Override
    public Map<String, AttributeValue> asDynamoDbJson() {
        Map<String, AttributeValue> map = new HashMap<>();
        map.put("Name", AttributeValue.builder().s(this.name).build());
        map.put("PhoneNumber", AttributeValue.builder().s(this.phoneNumber).build());
        return map;
    }
}
