package com.davidagood.awssdkv2.dynamodb.repository;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSortKey;

@DynamoDbBean
public class OrderItem {

    public static final String ORDER_TYPE = "Order";
    private static final String ORDER_PREFIX = "#ORDER#";
    private String id;
    private String customerId;

    public static String prefixedId(String id) {
        return ORDER_PREFIX + id;
    }

    @DynamoDbAttribute("OrderId")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @DynamoDbAttribute("CustomerId")
    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    @DynamoDbPartitionKey
    @DynamoDbAttribute("PK")
    public String getPartitionKey() {
        return CustomerItem.prefixedId(this.customerId);
    }

    // Do nothing, this is a derived attribute
    public void setPartitionKey(String partitionKey) {
    }

    @DynamoDbSortKey
    @DynamoDbAttribute("SK")
    public String getSortKey() {
        return prefixedId(this.id);
    }

    // Do nothing, this is a derived attribute
    public void setSortKey(String sortKey) {
    }

    @DynamoDbAttribute("Type")
    public String getType() {
        return ORDER_TYPE;
    }

    // Do nothing, this is a derived attribute
    // Note that without the setter, the attribute will silently not be persisted by the Enhanced Client
    public void setType(String type) {
        if (!ORDER_TYPE.equals(type)) {
            // This can happen when performing a Scan on a table of heterogeneous items
            throw new IllegalArgumentException("Attempted marshall into Order an item of Type=" + type);
        }
    }

    @Override
    public String toString() {
        return String.format("Order{id=%s, customerId=%s, PK=%s, SK=%s, Type=%s}",
                this.id, this.customerId, this.getPartitionKey(), this.getSortKey(), this.getType());
    }

}
