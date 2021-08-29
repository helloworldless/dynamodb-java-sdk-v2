package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.model.Customer;
import com.davidagood.awssdkv2.dynamodb.model.Order;
import com.davidagood.awssdkv2.dynamodb.model.Photo;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface DynamoDbItemMapper {

    DynamoDbItemMapper INSTANCE = Mappers.getMapper(DynamoDbItemMapper.class);

    Customer mapFromItem(CustomerItem customerItem);
    @Mapping(target = "partitionKey", ignore = true)
    @Mapping(target = "sortKey", ignore = true)
    @Mapping(target = "type", ignore = true)
    CustomerItem mapToItem(Customer customer);

    Order mapFromItem(OrderItem orderItem);
    @Mapping(target = "partitionKey", ignore = true)
    @Mapping(target = "sortKey", ignore = true)
    @Mapping(target = "type", ignore = true)
    OrderItem mapToItem(Order order);

    Photo mapFromItem(PhotoItem photoItem);
    PhotoItem mapToItem(Photo photo);

    PhotoItem.Metadata mapToItem(Photo.Metadata metadata);
    Photo.Metadata mapFromItem(PhotoItem.Metadata metadata);

}
