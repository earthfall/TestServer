package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;

@Configuration(proxyBeanMethods = false)
@Profile("local")
public class LocalDynamoDbConfiguration {
    @Bean
    AwsCredentialsProvider localAwsCredentialsProvider() {
        return StaticCredentialsProvider.create(AwsBasicCredentials.create("local", "local"));
    }
}
