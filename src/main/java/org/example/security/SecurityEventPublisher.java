package org.example.security;

import lombok.RequiredArgsConstructor;
import org.example.event.CustomEvent;
import org.example.event.ReactiveEventPublisher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityEventPublisher {

    @Autowired
    private final ReactiveEventPublisher eventPublisher;

    public void publishCustomEvent(final String message) {
        eventPublisher.publishEvent(new CustomEvent(message));
    }
}
