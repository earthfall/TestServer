package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.SdkRequest;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.interceptor.Context;
import software.amazon.awssdk.core.interceptor.ExecutionAttributes;
import software.amazon.awssdk.core.interceptor.ExecutionInterceptor;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.ProvisionedThroughputExceededException;

import java.net.URI;

@Configuration
public class DynamoDbConfig {

    boolean throttleDynamoDb = false;

    @Bean
    AwsCredentialsProvider awsCredentialsProvider(AwsProperties awsProperties) {
        return StaticCredentialsProvider.create(
            AwsBasicCredentials.create(
                awsProperties.aws().accessKey(),
                awsProperties.aws().secretKey()
            )
        );
    }

    @Bean
    DynamoDbClient dynamoDbClient(AwsProperties awsProperties) {
        return DynamoDbClient.builder()
            .region(Region.of(awsProperties.dynamoDb().region()))
            .endpointOverride(URI.create(awsProperties.dynamoDb().endpoint()))
            .overrideConfiguration(
                ClientOverrideConfiguration.builder()
                    .retryPolicy(RetryMode.defaultRetryMode())
                    .addExecutionInterceptor(new DynamoDbBehavior())
                    .build()
            )
            .build();
    }

    class DynamoDbBehavior implements ExecutionInterceptor {
        @Override
        public SdkRequest modifyRequest(Context.ModifyRequest context, ExecutionAttributes executionAttributes) {
            if (throttleDynamoDb) {
                throw ProvisionedThroughputExceededException.builder().message("injected").build();
            }

            return ExecutionInterceptor.super.modifyRequest(context, executionAttributes);
        }
    }
}
