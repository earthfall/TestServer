package org.example.aop;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.example.annotation.HasPermission;
import org.example.annotation.User;
import org.example.security.SecurityEvaluator;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;

@Log4j2
@RequiredArgsConstructor
@Aspect
@Component
public class SecurityAspect {

    private final SecurityEvaluator securityEvaluator;

    @Around(value = "@annotation(hasPermission)", argNames = "hasPermission")
    public Object handlePermission(ProceedingJoinPoint joinPoint, HasPermission hasPermission) throws Throwable {
        var permission = hasPermission.value();
        var signature = (MethodSignature) joinPoint.getSignature();
        var user = getUser(signature.getMethod(), joinPoint.getArgs());

        securityEvaluator.checkPermission(user, permission);
        return joinPoint.proceed();
    }

    private String getUser(Method method, Object[] args) {
        var annotations = method.getParameterAnnotations();
        for (int i = 0; i < method.getParameterCount(); i++) {
            if (hasUserAnnotation(annotations[i])) {
                return (String) args[i];
            }
        }
        return null;
    }

    private boolean hasUserAnnotation(Annotation[] annotAttr) {
        return Arrays.stream(annotAttr)
            .anyMatch(anno -> anno instanceof User);
    }

}
