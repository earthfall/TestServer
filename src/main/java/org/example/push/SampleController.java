package org.example.push;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.annotation.LogRequest;
import org.example.config.AppProperties;
import org.example.push.entity.FirebaseSendResponse;
import org.example.push.entity.PushEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@LogRequest
public class SampleController {
    private final AppProperties appProperties;
    private final DeviceNotificationService deviceNotificationService;

    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return appProperties.greeting() + ' ' + name + "!";
    }

    @PostMapping("/send")
    public FirebaseSendResponse send(@Valid @RequestBody PushEntity request) {
        return deviceNotificationService.send(request);
    }
}
