package org.example.push.entity;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PushDataEntity(@NotNull String entry) {
}
