package org.example.config;

import org.junit.jupiter.api.Test;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ListTablesRequest;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DynamoDbFailureInjectionTest {
    @Test
    void injectsThrottlingFromTestConfigurationOnly() {
        try (var client = DynamoDbClient.builder().region(software.amazon.awssdk.regions.Region.US_EAST_1)
            .endpointOverride(URI.create("http://localhost:1"))
            .credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials.create("test", "test")))
            .overrideConfiguration(config -> config.addExecutionInterceptor(new ExecutionInterceptor() {
                @Override
                public void beforeExecution(Context.BeforeExecution context, ExecutionAttributes attributes) {
                    throw ProvisionedThroughputExceededException.builder().message("injected by test").build();
                }
            })).build()) {
            assertThatThrownBy(() -> client.listTables(ListTablesRequest.builder().build()))
                .isInstanceOf(ProvisionedThroughputExceededException.class)
                .hasMessageContaining("injected by test");
        }
    }
}
