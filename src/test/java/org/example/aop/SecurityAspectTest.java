package org.example.aop;

import org.example.annotation.HasPermission;
import org.example.annotation.User;
import org.example.exception.NoPermissionException;
import org.example.security.SecurityEvaluator;
import org.example.security.UserService;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SecurityAspectTest {
    private final Target target = new Target();
    private final Target proxy = proxy(target);

    private static Target proxy(Target target) {
        var factory = new AspectJProxyFactory(target);
        factory.addAspect(new SecurityAspect(new SecurityEvaluator(new UserService())));
        return factory.getProxy();
    }

    @Test
    void rejectsMissingUserWithoutExecutingTarget() {
        assertThatThrownBy(() -> proxy.allowed(null)).isInstanceOf(NoPermissionException.class);
        assertThat(target.calls).isZero();
    }

    @Test
    void rejectsMissingAnnotationAndWrongUserType() {
        assertThatThrownBy(() -> proxy.missingAnnotation("world")).isInstanceOf(NoPermissionException.class);
        assertThatThrownBy(() -> proxy.wrongType(42)).isInstanceOf(NoPermissionException.class);
        assertThat(target.calls).isZero();
    }

    @Test
    void allowsValidUserAndRejectsBlankUser() {
        assertThat(proxy.allowed("world")).isEqualTo("allowed");
        assertThat(target.calls).isEqualTo(1);
        assertThatThrownBy(() -> proxy.allowed(" ")).isInstanceOf(NoPermissionException.class);
        assertThat(target.calls).isEqualTo(1);
    }

    public static class Target {
        int calls;
        @HasPermission(UserService.PERMISSION)
        public String allowed(@User @Nullable String user) { calls++; return "allowed"; }
        @HasPermission(UserService.PERMISSION)
        public String missingAnnotation(String user) { calls++; return "allowed"; }
        @HasPermission(UserService.PERMISSION)
        public String wrongType(@User int user) { calls++; return "allowed"; }
    }
}
