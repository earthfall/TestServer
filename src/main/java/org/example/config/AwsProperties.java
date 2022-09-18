package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "amazon")
@ConstructorBinding
public record AwsProperties(
    Aws aws,
    DynamoDb dynamoDb
) {
}

@ConstructorBinding
record Aws(
    String accessKey,
    String secretKey
) {
}

@ConstructorBinding
record DynamoDb(
    String endpoint,
    String region
) {
}

