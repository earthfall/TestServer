package org.example.push;

import lombok.RequiredArgsConstructor;
import org.example.aop.annotations.LogRequest;
import org.example.config.AppConfig;
import org.example.push.entity.PushDataEntity;
import org.example.push.entity.PushEntity;
import org.example.push.entity.PushNotificationEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RestController
@LogRequest
public class SampleController {

    private final AppConfig appConfig;

    private final DeviceNotificationService deviceNotificationService;

    /**
     * @api {get} /hello/:name Request User information
     * @apiName hello
     * @apiGroup User
     *
     * @apiParam {Number} id Users unique ID.
     *
     * @apiSuccess {String} firstname Firstname of the User.
     * @apiSuccess {String} lastname  Lastname of the User.
     *
     * @apiSuccessExample Success-Response:
     *     HTTP/1.1 200 OK
     *     {
     *       "firstname": "John",
     *       "lastname": "Doe"
     *     }
     *
     * @apiError UserNotFound The id of the User was not found.
     *
     * @apiErrorExample Error-Response:
     *     HTTP/1.1 404 Not Found
     *     {
     *       "error": "UserNotFound"
     *     }
     */
    @GetMapping("/hello/{name}")
    public String hello(@PathVariable String name) {
        return appConfig.getGreeting() + ' ' + name + "!";
    }

    @PostMapping("/send")
    public Mono<PushEntity> send() {
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

        return deviceNotificationService.send(entity);
    }
}
