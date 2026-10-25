package org.example.security;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

@Log4j2
@RequiredArgsConstructor
@Service
public class SecurityService {

    private final SecurityEventPublisher securityEventPublisher;

    String testHello() {
        log.debug("triggered");
        securityEventPublisher.publishCustomEvent("done");
        return "This hello";
    }
}
