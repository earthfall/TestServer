package org.example.security;

import org.jspecify.annotations.Nullable;
import lombok.RequiredArgsConstructor;
import org.example.exception.NoPermissionException;
import org.springframework.stereotype.Component;

import java.util.function.Supplier;

@RequiredArgsConstructor
@Component
public class SecurityEvaluator {

  private final UserService userService;

    public void checkPermission(@Nullable String userId, String permission) {
        if (!userService.hasPermission(userId, permission)) {
            throw new NoPermissionException(userId + " does not have permission: " + permission);
        }
    }

    public <T extends @Nullable Object> T confirmPermission(@Nullable String userId, String permission, Supplier<T> func) {
        checkPermission(userId, permission);
        return func.get();
    }
}
