package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.config.FirebaseConfig;
import org.example.entity.PushEntity;
import org.springframework.http.HttpEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

@RequiredArgsConstructor
@Service
public class DeviceNotificationService {

    private final FirebaseConfig firebaseConfig;

    @Async
    public CompletableFuture<PushEntity> send(PushEntity entity) {

        RestTemplate restTemplate = new RestTemplate();

        ArrayList<ClientHttpRequestInterceptor> interceptors = new ArrayList<>();
        interceptors.add(new HeaderRequestInterceptor("Authorization", "key=" + firebaseConfig.getServerKey()));
        interceptors.add(new HeaderRequestInterceptor("Content-Type", "application/json"));
        restTemplate.setInterceptors(interceptors);

        return CompletableFuture.completedFuture(
                restTemplate.postForObject(firebaseConfig.getApiUrl(), new HttpEntity<>(entity), PushEntity.class)
        );
    }
}
