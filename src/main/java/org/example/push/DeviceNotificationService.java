package org.example.push;

import lombok.RequiredArgsConstructor;
import org.example.config.FirebaseProperties;
import org.example.push.entity.PushEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DeviceNotificationService {

    private final FirebaseProperties firebaseProperties;

    public Mono<PushEntity> send(PushEntity entity) {
        return WebClient.create()
            .post()
            .uri(firebaseProperties.apiUrl())
            .header("Authorization", "key=" + firebaseProperties.serverKey())
            .header("Content-Type", "application/json")
            .bodyValue(entity)
            .retrieve()
            .bodyToMono(PushEntity.class);
    }
}
