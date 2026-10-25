package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;
import jakarta.validation.constraints.NotBlank;

@Validated
@ConfigurationProperties(prefix = "test")
public record AppProperties(
    int id,
    @NotBlank String greeting
) {
}
