package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@Service
public class UserService {
    public static final String PERMISSION = "permission";

    public Mono<Boolean> hasPermission(String user, String permission) {
        return Mono.just(PERMISSION.equals(permission));
    }
}
