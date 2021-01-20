package com.davidagood.awssdkv2.dynamodb.model;

import lombok.Data;

import java.time.Instant;

@Data
public class Photo {
    private String id;
    private Metadata metadata;

    @Data
    public static class Metadata {
        private String device;
        private Double latitude;
        private Double longitude;
        private Instant timestamp;
    }

}

