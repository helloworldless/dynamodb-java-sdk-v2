package com.davidagood.awssdkv2.dynamodb.repository;

public class DynamoDbEntityAlreadyExistsException extends RuntimeException {
    public DynamoDbEntityAlreadyExistsException(String message) {
        super(message);
    }
}
