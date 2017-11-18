package org.example.controller;

import org.example.config.AppConfig;
import org.example.entity.PushDataEntity;
import org.example.entity.PushEntity;
import org.example.entity.PushNotificationEntity;
import org.example.service.DeviceNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.springframework.http.HttpStatus.OK;

@RestController
public class SampleController {

    @Autowired
    private AppConfig appConfig;

    @Autowired
    private DeviceNotificationService deviceNotificationService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return appConfig.getGreeting() + ' ' + name + "!";
    }

    @PostMapping("/send")
    public ResponseEntity<PushEntity> send() throws ExecutionException, InterruptedException {
        PushEntity entity = PushEntity.builder()
                .notification(PushNotificationEntity.builder()
                        .title("humanpoc notification")
                        .body("header")
                        .build()
                )
                .data(PushDataEntity.builder()
                        .entry("test")
                        .build()
                )
                .to("/server/send")
                .priority("high")
                .build();

        CompletableFuture<PushEntity> pushNotification = deviceNotificationService.send(new HttpEntity<>(entity));
        CompletableFuture.allOf(pushNotification)
                .join();

        return new ResponseEntity<>(pushNotification.get(), OK);
    }
}
