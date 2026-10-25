package org.example.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.jspecify.annotations.Nullable;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "amazon")
public record AwsProperties(@NotNull @Valid DynamoDb dynamoDb) {
    public record DynamoDb(@Nullable URI endpoint, @NotBlank String region) {
        public DynamoDb {
            if (endpoint != null && (!("http".equals(endpoint.getScheme()) || "https".equals(endpoint.getScheme()))
                || endpoint.getHost() == null)) {
                throw new IllegalArgumentException("DynamoDB endpoint must be an absolute HTTP(S) URL");
            }
        }
    }
}
