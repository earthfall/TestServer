package org.example.push.entity;

import lombok.Builder;

@Builder
public record PushDataEntity(
    String entry
) {
}
