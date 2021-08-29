package com.davidagood.awssdkv2.dynamodb.repository;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughput;

import java.util.UUID;

/*
 * Source: https://github.com/aws/aws-sdk-java-v2/blob/86b05d40718cb9c4c020d141262e01d4408e22a9/services-custom/dynamodb-enhanced/src/test/java/software/amazon/awssdk/enhanced/dynamodb/functionaltests/LocalDynamoDbTestBase.java
 */
public class LocalDynamoDbTestBase {
    private static final LocalDynamoDb localDynamoDb = new LocalDynamoDb();
    private static final ProvisionedThroughput DEFAULT_PROVISIONED_THROUGHPUT =
            ProvisionedThroughput.builder()
                    .readCapacityUnits(50L)
                    .writeCapacityUnits(50L)
                    .build();

    private final String uniqueTableSuffix = UUID.randomUUID().toString();

    @BeforeAll
    public static void initializeLocalDynamoDb() {
        localDynamoDb.start();
    }

    @AfterAll
    public static void stopLocalDynamoDb() {
        localDynamoDb.stop();
    }

    protected static LocalDynamoDb localDynamoDb() {
        return localDynamoDb;
    }

    protected String getConcreteTableName(String logicalTableName) {
        return logicalTableName + "_" + uniqueTableSuffix;

    }

    protected ProvisionedThroughput getDefaultProvisionedThroughput() {
        return DEFAULT_PROVISIONED_THROUGHPUT;
    }
}
