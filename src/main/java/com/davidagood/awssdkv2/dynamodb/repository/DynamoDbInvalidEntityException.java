package com.davidagood.awssdkv2.dynamodb.repository;

public class DynamoDbInvalidEntityException extends RuntimeException {
    public DynamoDbInvalidEntityException(String message) {
        super(message);
    }
}
