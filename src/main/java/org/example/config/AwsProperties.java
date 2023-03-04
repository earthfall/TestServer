package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "amazon")
public record AwsProperties(
    Aws aws,
    DynamoDb dynamoDb
) {
}

record Aws(
    String accessKey,
    String secretKey
) {
}

record DynamoDb(
    String endpoint,
    String region
) {
}

