package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.SuperTypeToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class JsonAttributeConverter<T> implements AttributeConverter<T> {

    private final Type type;
    private final ObjectMapper objectMapper;

    public JsonAttributeConverter(Type type, ObjectMapper objectMapper) {
        this.type = type;
        this.objectMapper = objectMapper;
    }

    public static <T> JsonAttributeConverter<T> create(SuperTypeToken<T> typeReference, ObjectMapper objectMapper) {
        Type type = typeReference.getType();
        return new JsonAttributeConverter<>(type, objectMapper);
    }

    @SneakyThrows
    @Override
    public AttributeValue transformFrom(T input) {
        return AttributeValue.builder().s(objectMapper.writeValueAsString(input)).build();
    }

    @SneakyThrows
    @Override
    public T transformTo(AttributeValue input) {
        return objectMapper.readValue(input.s(), this.type().rawClass());
    }

    @Override
    @SuppressWarnings({"unchecked"})
    public EnhancedType<T> type() {
        Class<?> rawType = type instanceof Class<?>
            ? (Class<?>) type
            : (Class<?>) ((ParameterizedType) type).getRawType();
        Class<T> classType = (Class<T>) rawType;
        return EnhancedType.of(classType);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.S;
    }

}
