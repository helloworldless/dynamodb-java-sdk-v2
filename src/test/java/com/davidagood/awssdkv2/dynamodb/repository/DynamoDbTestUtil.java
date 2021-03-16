package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.services.dynamodb.model.*;

class DynamoDbTestUtil {

    private DynamoDbTestUtil() {
    }

    static CreateTableRequest createTableRequest(String tableName) {
        var pk = KeySchemaElement.builder().attributeName("PK").keyType(KeyType.HASH).build();
        var pkDef = AttributeDefinition.builder().attributeName("PK").attributeType(ScalarAttributeType.S).build();
        var sk = KeySchemaElement.builder().attributeName("SK").keyType(KeyType.RANGE).build();
        var skDef = AttributeDefinition.builder().attributeName("SK").attributeType(ScalarAttributeType.S).build();
        return CreateTableRequest.builder()
            .tableName(tableName)
            .keySchema(pk, sk)
            .attributeDefinitions(pkDef, skDef)
            .billingMode(BillingMode.PAY_PER_REQUEST)
            .build();
    }

}
