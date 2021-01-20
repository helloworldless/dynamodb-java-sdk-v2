package com.davidagood.awssdkv2.dynamodb.repository;

import com.davidagood.awssdkv2.dynamodb.model.Photo;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

import java.time.LocalDate;
import java.time.ZoneId;

import static com.davidagood.awssdkv2.dynamodb.App.MAPPER;
import static org.assertj.core.api.Assertions.assertThat;

class JsonAttributeConverterTest extends LocalDynamoDbSyncTestBase {

    static final String tableName = "JsonConverterTestTable";

    DynamoDbRepository repository = new DynamoDbRepository(getDynamoDbClient(), getConcreteTableName(tableName), MAPPER);

    @BeforeEach
    void createTable() {
        System.out.println("TableName=" + getConcreteTableName(tableName));
        repository.getPhotoTable().createTable();
    }

    @AfterEach
    void deleteTable() {
        getDynamoDbClient().deleteTable(DeleteTableRequest.builder()
            .tableName(getConcreteTableName(tableName))
            .build());
    }

    @Test
    void roundTripTest() {
        var photoId = "123";
        var photo = new Photo();
        photo.setId(photoId);
        var metadata = new Photo.Metadata();
        metadata.setDevice("iPhone X");
        metadata.setLatitude(64.76809d);
        metadata.setLongitude(-103.75976d);
        metadata.setTimestamp(LocalDate.of(2021, 1, 20).atStartOfDay(ZoneId.of("America/New_York")).toInstant());
        photo.setMetadata(metadata);
        repository.insertPhoto(photo);

        assertThat(repository.findPhoto(photoId)).contains(photo);
    }

}
