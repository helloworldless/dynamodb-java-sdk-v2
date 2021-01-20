package com.davidagood.awssdkv2.dynamodb;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/*
 * Source: http://gafter.blogspot.com/2006/12/super-type-tokens.html
 */
public abstract class SuperTypeToken<T> {

    private final Type type;

    protected SuperTypeToken() {
        Type superclass = getClass().getGenericSuperclass();
        if (superclass instanceof Class) {
            throw new RuntimeException("Missing type parameter.");
        }
        this.type = ((ParameterizedType) superclass).getActualTypeArguments()[0];
    }

    public Type getType() {
        return this.type;
    }

}