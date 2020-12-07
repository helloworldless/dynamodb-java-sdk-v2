package com.davidagood.awssdkv2.dynamodb;

public class DynamoDbEntityAlreadyExistsException extends RuntimeException {
    public DynamoDbEntityAlreadyExistsException(String message) {
        super(message);
    }
}
