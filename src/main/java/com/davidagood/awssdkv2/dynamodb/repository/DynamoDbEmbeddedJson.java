package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

public interface DynamoDbEmbeddedJson {
    AttributeValue toJson();
}
