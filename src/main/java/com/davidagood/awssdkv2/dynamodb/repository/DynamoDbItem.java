package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.Map;

public interface DynamoDbItem {
    Map<String, AttributeValue> asDynamoDbJson();
}
