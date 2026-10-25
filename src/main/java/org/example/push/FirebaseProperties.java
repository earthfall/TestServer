package org.example.push;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.net.URI;

@Validated
@ConfigurationProperties(prefix = "firebase")
public record FirebaseProperties(@NotBlank String projectId, @NotNull URI apiUrl) {
    public FirebaseProperties {
        if (apiUrl != null && (!("http".equals(apiUrl.getScheme()) || "https".equals(apiUrl.getScheme()))
            || apiUrl.getHost() == null)) {
            throw new IllegalArgumentException("Firebase API URL must be an absolute HTTP(S) URL");
        }
    }
}
