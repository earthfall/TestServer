package org.example.push.entity;

import lombok.Builder;

@Builder
public record PushEntity(
    PushNotificationEntity notification,
    PushDataEntity data,
    String to,
    String priority
) {
}
