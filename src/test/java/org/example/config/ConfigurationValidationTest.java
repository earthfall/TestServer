package org.example.config;

import org.example.push.FirebaseProperties;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.TestConfiguration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationValidationTest {
    private final ApplicationContextRunner runner = new ApplicationContextRunner()
        .withUserConfiguration(PropertiesConfiguration.class)
        .withPropertyValues("test.greeting=Hello", "amazon.dynamo-db.region=us-east-1",
            "firebase.project-id=test-project", "firebase.api-url=https://fcm.googleapis.com");

    @Test
    void failsOnBlankRequiredConfiguration() {
        runner.withPropertyValues("test.greeting=").run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("amazon.dynamo-db.region=").run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("firebase.project-id=").run(context -> assertThat(context).hasFailed());
    }

    @Test
    void rejectsInvalidEndpoint() {
        runner.withPropertyValues("amazon.dynamo-db.endpoint=relative/path")
            .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("amazon.dynamo-db.endpoint=ftp://localhost")
            .run(context -> assertThat(context).hasFailed());
        runner.withPropertyValues("firebase.api-url=relative/path")
            .run(context -> assertThat(context).hasFailed());
    }

    @Test
    void usesDefaultCredentialChainOutsideLocalProfile() {
        runner.withUserConfiguration(DynamoDbConfiguration.class, LocalDynamoDbConfiguration.class)
            .run(context -> {
                assertThat(context).hasNotFailed().hasSingleBean(DynamoDbClient.class);
                assertThat(context.getBean(AwsCredentialsProvider.class))
                    .isInstanceOf(DefaultCredentialsProvider.class);
            });
    }

    @Test
    void localProfileUsesOnlyLocalCredentials() {
        runner.withPropertyValues("spring.profiles.active=local",
                "amazon.dynamo-db.endpoint=http://localhost:8000")
            .withUserConfiguration(DynamoDbConfiguration.class, LocalDynamoDbConfiguration.class)
            .run(context -> {
                assertThat(context).hasNotFailed().hasSingleBean(AwsCredentialsProvider.class);
                var credentials = context.getBean(AwsCredentialsProvider.class);
                assertThat(credentials).isInstanceOf(StaticCredentialsProvider.class);
                assertThat(credentials.resolveCredentials().accessKeyId()).isEqualTo("local");
            });
    }

    @TestConfiguration(proxyBeanMethods = false)
    @EnableConfigurationProperties({AppProperties.class, AwsProperties.class, FirebaseProperties.class})
    static class PropertiesConfiguration {
    }
}
