package org.example.push.entity;

import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PushEntity(
    @Nullable PushNotificationEntity notification,
    @Nullable PushDataEntity data,
    String to,
    @Nullable String priority
) {
}
