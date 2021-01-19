package com.davidagood.awssdkv2.dynamodb;

import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.model.Order;
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
