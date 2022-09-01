package org.example.config;

import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "amazon")
@ConstructorBinding
@Value
public class AwsProperties {
    Aws aws;
    Dynamodb dynamoDb;

    @ConstructorBinding
    @Value
    static class Aws {
        String accessKey;
        String secretKey;
    }

    @ConstructorBinding
    @Value
    static class Dynamodb {
        String endpoint;
        String region;
    }
}
