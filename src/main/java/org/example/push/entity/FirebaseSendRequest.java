package org.example.push.entity;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.jspecify.annotations.Nullable;

import java.util.Map;

public record FirebaseSendRequest(Message message) {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Message(
        @Nullable String token,
        @Nullable String topic,
        @Nullable PushNotificationEntity notification,
        @Nullable Map<String, String> data,
        @Nullable Android android
    ) {
    }

    public record Android(String priority) {
    }
}
