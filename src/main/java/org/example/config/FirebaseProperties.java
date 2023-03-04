package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix="firebase")
public record FirebaseProperties(
    String serverKey,
    String apiUrl
) {
}
