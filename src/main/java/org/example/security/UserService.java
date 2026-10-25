package org.example.security;

import org.jspecify.annotations.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    public static final String PERMISSION = "permission";

    public boolean hasPermission(@Nullable String user, String permission) {
        return user != null && !user.isBlank() && PERMISSION.equals(permission);
    }
}
