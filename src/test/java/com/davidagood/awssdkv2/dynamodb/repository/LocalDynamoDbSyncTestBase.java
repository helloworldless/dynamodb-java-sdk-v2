package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

/*
 * Source:https://github.com/aws/aws-sdk-java-v2/blob/86b05d40718cb9c4c020d141262e01d4408e22a9/services-custom/dynamodb-enhanced/src/test/java/software/amazon/awssdk/enhanced/dynamodb/functionaltests/LocalDynamoDbSyncTestBase.java
 */
public class LocalDynamoDbSyncTestBase extends LocalDynamoDbTestBase {

    private DynamoDbClient dynamoDbClient = localDynamoDb().createClient();

    protected DynamoDbClient getDynamoDbClient() {
        return dynamoDbClient;
    }
}