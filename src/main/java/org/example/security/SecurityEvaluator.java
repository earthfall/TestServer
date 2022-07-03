package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.exception.NoPermissionException;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class SecurityEvaluator {

  private final UserService userService;

    public <T> Mono<T> confirmPermission(String userId, String permission, Supplier<Mono<T>> func) {
        return userService
            .hasPermission(userId, permission)
            .flatMap(it ->
                it
                    ? func.get()
                    : Mono.error(new NoPermissionException(userId + " does not have permission: " + permission))
            );
    }
}
