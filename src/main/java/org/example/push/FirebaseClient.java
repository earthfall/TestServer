package org.example.push;

import org.example.push.entity.FirebaseSendRequest;
import org.example.push.entity.FirebaseSendResponse;
import org.jspecify.annotations.Nullable;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.service.annotation.PostExchange;

public interface FirebaseClient {
    @PostExchange(url = "/v1/projects/{projectId}/messages:send", contentType = MediaType.APPLICATION_JSON_VALUE)
    @Nullable FirebaseSendResponse send(@PathVariable String projectId,
        @RequestHeader("Authorization") String authorization, @RequestBody FirebaseSendRequest request);
}
