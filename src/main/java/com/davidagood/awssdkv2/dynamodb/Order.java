package com.davidagood.awssdkv2.dynamodb;

import lombok.NonNull;
import lombok.Value;

@Value
public class Order {

    @NonNull
    String id;

    @NonNull
    String customerId;

}
