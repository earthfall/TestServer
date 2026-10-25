package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

@Configuration(proxyBeanMethods = false)
public class DynamoDbConfiguration {
    @Bean
    @Profile("!local")
    AwsCredentialsProvider awsCredentialsProvider() {
        return DefaultCredentialsProvider.builder().build();
    }

    @Bean
    DynamoDbClient dynamoDbClient(AwsProperties properties, AwsCredentialsProvider credentialsProvider) {
        var builder = DynamoDbClient.builder()
            .credentialsProvider(credentialsProvider)
            .region(Region.of(properties.dynamoDb().region()));
        var endpoint = properties.dynamoDb().endpoint();
        if (endpoint != null) {
            builder.endpointOverride(endpoint);
        }
        return builder.build();
    }
}
