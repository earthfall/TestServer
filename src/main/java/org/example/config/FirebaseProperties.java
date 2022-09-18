package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConstructorBinding
@ConfigurationProperties(prefix="firebase")
public record FirebaseProperties(
    String serverKey,
    String apiUrl
) {
}
