package org.example.push.entity;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import org.jspecify.annotations.Nullable;

@Builder
public record PushEntity(
    @Valid @Nullable PushNotificationEntity notification,
    @Valid @Nullable PushDataEntity data,
    @NotBlank @Pattern(regexp = "(?!/topics/$)\\S+") String to,
    @Nullable @Pattern(regexp = "high|normal") String priority
) {
}
