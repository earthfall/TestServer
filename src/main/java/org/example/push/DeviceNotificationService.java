package org.example.push;

import lombok.RequiredArgsConstructor;
import org.example.config.FirebaseConfig;
import org.example.push.entity.PushEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class DeviceNotificationService {

    private final FirebaseConfig firebaseConfig;

    @Async
    public Mono<PushEntity> send(PushEntity entity) {
        return WebClient.create()
                .post()
                .uri(firebaseConfig.getApiUrl())
                .header("Authorization", "key=" + firebaseConfig.getServerKey())
                .header("Content-Type", "application/json")
                .bodyValue(entity)
                .retrieve()
                .bodyToMono(PushEntity.class);
    }
}
