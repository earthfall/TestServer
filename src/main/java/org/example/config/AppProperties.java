package org.example.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "test")
public record AppProperties(
    int id,
    String greeting
) {
}
