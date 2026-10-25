package org.example.push.entity;

import lombok.Builder;

@Builder
public record PushNotificationEntity(
    String title,
    String body
) {
}
