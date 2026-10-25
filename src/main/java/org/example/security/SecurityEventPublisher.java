package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.event.CustomEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishCustomEvent(final String message) {
        eventPublisher.publishEvent(new CustomEvent(message));
    }
}
