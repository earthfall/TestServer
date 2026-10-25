package org.example.push;

import com.google.auth.oauth2.GoogleCredentials;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.client.RestClientException;
import org.springframework.web.service.registry.ImportHttpServices;

import java.io.IOException;
import java.util.List;

@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(FirebaseProperties.class)
@ImportHttpServices(group = "firebase", types = FirebaseClient.class)
public class FirebaseClientConfiguration {
    @Bean
    @Lazy
    GoogleCredentials firebaseCredentials() throws IOException {
        return GoogleCredentials.getApplicationDefault()
            .createScoped(List.of("https://www.googleapis.com/auth/firebase.messaging"));
    }

    @Bean
    FirebaseAccessTokenProvider firebaseAccessTokenProvider(ObjectProvider<GoogleCredentials> credentialsProvider) {
        return () -> {
            try {
                var credentials = credentialsProvider.getObject();
                credentials.refreshIfExpired();
                var token = credentials.getAccessToken();
                if (token == null || token.getTokenValue().isBlank()) {
                    throw new RestClientException("Firebase access token is unavailable");
                }
                return token.getTokenValue();
            } catch (IOException | BeansException exception) {
                throw new RestClientException("Firebase authentication failed", exception);
            }
        };
    }
}
