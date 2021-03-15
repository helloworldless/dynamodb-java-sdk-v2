package com.davidagood.awssdkv2.dynamodb.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.Map;
import java.util.Set;

import static com.davidagood.awssdkv2.dynamodb.App.MAPPER;

@Value
@AllArgsConstructor
@Builder
public class BeanlessNestedJson implements DynamoDbEmbeddedJson {

    @NonNull
    String id;

    @NonNull
    Instant createdAt;

    @NonNull
    Map<String, String> additionalAttributes;

    @NonNull
    Set<String> words;

    public static BeanlessNestedJson fromJson(String json) {
        try {
            return MAPPER.readValue(json, BeanlessNestedJson.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to deserialize BeanlessNestedJson; Error: " + e);
        }
    }

    @Override
    public AttributeValue toJson() {
        try {
            return AttributeValue.builder().s(MAPPER.writeValueAsString(this)).build();
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to serialize BeanlessNestedJson; Error: " + e);
        }
    }

}
