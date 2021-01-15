package com.davidagood.awssdkv2.dynamodb;

import lombok.NonNull;
import lombok.Value;

@Value
public class Customer {
    @NonNull
    String id;
}
