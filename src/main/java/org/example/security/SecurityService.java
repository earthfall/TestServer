package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class SecurityService {

    Mono<String> testHello() {
        return Mono.just("This hello");
    }
}
