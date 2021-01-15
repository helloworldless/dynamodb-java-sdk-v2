package com.davidagood.awssdkv2.dynamodb;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class CustomerWithOrders {

    @NonNull
    Customer customer;

    @NonNull
    List<Order> orders;

}
