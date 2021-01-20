package com.davidagood.awssdkv2.dynamodb.repository;

import com.amazonaws.services.dynamodbv2.local.main.ServerRunner;
import com.amazonaws.services.dynamodbv2.local.server.DynamoDBProxyServer;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/*
 * Source: https://github.com/aws/aws-sdk-java-v2/blob/86b05d40718cb9c4c020d141262e01d4408e22a9/services-custom/dynamodb-enhanced/src/test/java/software/amazon/awssdk/enhanced/dynamodb/functionaltests/LocalDynamoDb.java
 */
class LocalDynamoDb {
    private DynamoDBProxyServer server;
    private int port;

    private static RuntimeException propagate(Exception e) {
        if (e instanceof RuntimeException) {
            throw (RuntimeException) e;
        }
        throw new RuntimeException(e);
    }

    /*
     * Start the local DynamoDb service and run in background
     */
    void start() {
        port = getFreePort();
        String portString = Integer.toString(port);

        try {
            server = createServer(portString);
            server.start();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    /**
     * Create a standard AWS v2 SDK client pointing to the local DynamoDb instance
     *
     * @return A DynamoDbClient pointing to the local DynamoDb instance
     */
    DynamoDbClient createClient() {
        String endpoint = String.format("http://localhost:%d", port);
        return DynamoDbClient.builder()
                .endpointOverride(URI.create(endpoint))
                // The region is meaningless for local DynamoDb but required for client builder validation
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                .overrideConfiguration(o -> o.addExecutionInterceptor(new VerifyUserAgentInterceptor()))
                .build();
    }

    DynamoDbAsyncClient createAsyncClient() {
        String endpoint = String.format("http://localhost:%d", port);
        return DynamoDbAsyncClient.builder()
                .endpointOverride(URI.create(endpoint))
                .region(Region.US_EAST_1)
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create("dummy-key", "dummy-secret")))
                .overrideConfiguration(o -> o.addExecutionInterceptor(new VerifyUserAgentInterceptor()))
                .build();
    }

    /**
     * Stops the local DynamoDb service and frees up resources it is using.
     */
    void stop() {
        try {
            server.stop();
        } catch (Exception e) {
            throw propagate(e);
        }
    }

    private DynamoDBProxyServer createServer(String portString) throws Exception {
        return ServerRunner.createServerFromCommandLineArgs(
                new String[]{
                        "-inMemory",
                        "-port", portString
                });
    }

    private int getFreePort() {
        try {
            ServerSocket socket = new ServerSocket(0);
            int port = socket.getLocalPort();
            socket.close();
            return port;
        } catch (IOException ioe) {
            throw propagate(ioe);
        }
    }

    private static class VerifyUserAgentInterceptor implements ExecutionInterceptor {

        @Override
        public void beforeTransmission(Context.BeforeTransmission context, ExecutionAttributes executionAttributes) {
            Optional<String> headers = context.httpRequest().firstMatchingHeader("User-agent");
            assertThat(headers).isPresent();
            assertThat(headers.get()).contains("hll/ddb-enh");
        }
    }

}