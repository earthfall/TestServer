package org.example.security;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService {
    public static final String PERMISSION = "permission";

    public boolean hasPermission(String user, String permission) {
        return PERMISSION.equals(permission);
    }
}
