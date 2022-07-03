package org.example.aop;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Log4j2
@Aspect
@Component
public class LoggingAspect {

    @Pointcut("execution(public * *(..))")
    private void publicMethod() {
        // empty
    }

    @Pointcut("@within(org.example.aop.annotations.LogRequest)")
    private void annotatedWithLogRequest() {
        // empty
    }

    @Before("publicMethod() && annotatedWithLogRequest()")
    public void logMethod(JoinPoint jp) {
        String methodName = jp.getSignature().getName();
        log.info("Executing method: " + methodName);
    }
}
