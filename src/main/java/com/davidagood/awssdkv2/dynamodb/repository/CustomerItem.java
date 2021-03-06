package com.davidagood.awssdkv2.dynamodb.repository;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
@EqualsAndHashCode
@ToString
public class CustomerItem {

    static final String A_RECORD = "A";
    static final String CUSTOMER_TYPE = "Customer";
    private static final String CUSTOMER_PREFIX = "CUSTOMER#";

    static String prefixedId(String id) {
        return CUSTOMER_PREFIX + id;
    }

    private String id;

    @DynamoDbAttribute("CustomerId")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPartitionKey() {
        return prefixedId(this.id);
    }

    public void setPartitionKey(String partitionKey) {
        // Do nothing, this is a derived attribute
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSortKey() {
        return A_RECORD;
    }

    public void setSortKey(String sortKey) {
        // Do nothing, this is a derived attribute
    }

    @DynamoDbAttribute("Type")
    public String getType() {
        return CUSTOMER_TYPE;
    }

    // Do nothing, this is a derived attribute
    // Note that without the setter, the attribute will silently not be persisted by the Enhanced Client
    public void setType(String type) {
        if (!CUSTOMER_TYPE.equals(type)) {
            // This can happen when performing a Scan on a table of heterogeneous items
            throw new IllegalArgumentException("Attempted marshall into Customer an item of Type=" + type);
        }
    }

}
