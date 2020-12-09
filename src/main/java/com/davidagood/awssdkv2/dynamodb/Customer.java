package com.davidagood.awssdkv2.dynamodb;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

@DynamoDbBean
public class Customer {

    public static final String A_RECORD = "A";
    public static final String CUSTOMER_TYPE = "Customer";
    private static final String CUSTOMER_PREFIX = "CUSTOMER#";

    public static String prefixedId(String id) {
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

    // Do nothing, this is a derived attribute
    public void setPartitionKey(String partitionKey) {
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSortKey() {
        return A_RECORD;
    }

    // Do nothing, this is a derived attribute
    public void setSortKey(String sortKey) {
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

    @Override
    public String toString() {
        return String.format("Customer{id=%s, PK=%s, SK=%s, Type=%s}",
                this.id, this.getPartitionKey(), this.getSortKey(), this.getType());
    }

}
