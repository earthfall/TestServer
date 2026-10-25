package org.example.push.entity;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record PushNotificationEntity(@NotBlank String title, @NotBlank String body) {
}
