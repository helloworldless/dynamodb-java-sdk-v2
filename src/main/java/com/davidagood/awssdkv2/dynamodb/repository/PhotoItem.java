package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.SuperTypeToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.time.Instant;

import static software.amazon.awssdk.enhanced.dynamodb.mapper.StaticAttributeTags.primaryPartitionKey;

@Data
class PhotoItem {

    private String id;
    private Metadata metadata;

    static TableSchema<PhotoItem> schema(ObjectMapper mapper) {
        SuperTypeToken<Metadata> superTypeToken = new SuperTypeToken<>() {
        };
        JsonAttributeConverter<Metadata> converter = JsonAttributeConverter.create(superTypeToken, mapper);
        return TableSchema.builder(PhotoItem.class)
            .newItemSupplier(PhotoItem::new)
            .addAttribute(String.class, a -> {
                a.name("id");
                a.getter(PhotoItem::getId);
                a.setter(PhotoItem::setId);
                a.tags(primaryPartitionKey());
            })
            .addAttribute(Metadata.class, a -> {
                a.name("metadata");
                a.getter(PhotoItem::getMetadata);
                a.setter(PhotoItem::setMetadata);
                a.attributeConverter(converter);
            })
            .build();
    }

    @Data
    static class Metadata {
        private String device;
        private Double latitude;
        private Double longitude;
        private Instant timestamp;
    }

}

