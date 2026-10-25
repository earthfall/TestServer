package org.example.push;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.push.entity.FirebaseSendRequest;
import org.example.push.entity.FirebaseSendResponse;
import org.example.push.entity.PushEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.client.RestClientException;

import java.util.Locale;
import java.util.Map;

@Service
@Validated
@RequiredArgsConstructor
public class DeviceNotificationService {
    private final FirebaseClient firebaseClient;
    private final FirebaseProperties properties;
    private final FirebaseAccessTokenProvider tokenProvider;

    public FirebaseSendResponse send(@Valid PushEntity entity) {
        var destination = entity.to();
        var topic = destination.startsWith("/topics/") ? destination.substring("/topics/".length()) : null;
        var data = entity.data();
        var priority = entity.priority();
        var message = new FirebaseSendRequest.Message(
            topic == null ? destination : null, topic, entity.notification(),
            data == null ? null : Map.of("entry", data.entry()),
            priority == null ? null : new FirebaseSendRequest.Android(priority.toUpperCase(Locale.ROOT)));
        var response = firebaseClient.send(properties.projectId(), "Bearer " + tokenProvider.accessToken(),
            new FirebaseSendRequest(message));
        if (response == null || response.name() == null || response.name().isBlank()) {
            throw new RestClientException("Firebase returned an empty or invalid response");
        }
        return response;
    }
}
