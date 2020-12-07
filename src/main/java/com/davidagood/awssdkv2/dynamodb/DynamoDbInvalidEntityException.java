package com.davidagood.awssdkv2.dynamodb;

public class DynamoDbInvalidEntityException extends RuntimeException {
    public DynamoDbInvalidEntityException(String message) {
        super(message);
    }
}
