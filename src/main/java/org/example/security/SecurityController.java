package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.example.aop.annotations.HasPermission;
import org.example.aop.annotations.User;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import org.example.security.model.Message;

@Log4j2
@RequiredArgsConstructor
@RestController
public class SecurityController {

    private final SecurityService securityService;
    private final SecurityEvaluator securityEvaluator;

    @GetMapping("/permission/{name}")
    public Mono<String> hello(@PathVariable String name) {
        return securityEvaluator.confirmPermission(
            name,
            "test1",
            securityService::testHello
        );
    }

    @GetMapping("/permission-valid/{name}")
    public Mono<String> validHello(@PathVariable String name) {
        return securityEvaluator.confirmPermission(
            name,
            UserService.PERMISSION,
            securityService::testHello
        );
    }

    @HasPermission("test1")
    @GetMapping("/permission-aop/{name}")
    public Mono<String> helloAop(@User @PathVariable String name) {
        log.debug("Received aop request with " + name);
        return securityService.testHello();
    }

    @GetMapping(value = "/permission-valid/{name}", produces = MediaType.APPLICATION_PROTOBUF_VALUE)
    public Mono<Message> helloAopProto(@User @PathVariable String name) {
        log.debug("Received protobuf request with " + name);
        return securityService.testHello()
            .map(it ->
                Message.newBuilder().setName(it).build()
            );
    }
}
