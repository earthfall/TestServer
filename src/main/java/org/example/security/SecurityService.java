package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Log4j2
@RequiredArgsConstructor
@Service
public class SecurityService {

    private final SecurityEventPublisher securityEventPublisher;

    Mono<String> testHello() {
        return Mono.just("This hello")
            .doOnSuccess(it -> log.debug("triggered"))
            .doOnSuccess(it -> securityEventPublisher.publishCustomEvent("done"));
    }
}
