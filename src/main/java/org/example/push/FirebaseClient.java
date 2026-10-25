package org.example.push;

import org.example.push.entity.PushEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.service.annotation.PostExchange;

public interface FirebaseClient {

    @PostExchange(contentType = MediaType.APPLICATION_JSON_VALUE)
    PushEntity send(@RequestBody PushEntity entity);
}
