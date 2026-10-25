package org.example.push;

import org.example.config.FirebaseProperties;
import org.example.push.entity.PushEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class DeviceNotificationService {

    private final FirebaseProperties firebaseProperties;
    private final RestClient restClient;

    public DeviceNotificationService(FirebaseProperties firebaseProperties, RestClient.Builder builder) {
        this.firebaseProperties = firebaseProperties;
        this.restClient = builder.build();
    }

    public PushEntity send(PushEntity entity) {
        return restClient
            .post()
            .uri(firebaseProperties.apiUrl())
            .header("Authorization", "key=" + firebaseProperties.serverKey())
            .header("Content-Type", "application/json")
            .body(entity)
            .retrieve()
            .body(PushEntity.class);
    }
}
