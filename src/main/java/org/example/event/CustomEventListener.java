package org.example.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Log4j2
@Component
@RequiredArgsConstructor
public class CustomEventListener implements InitializingBean {

    @Autowired
    private final ReactiveEventPublisher eventPublisher;

    @Override
    public void afterPropertiesSet() {
        eventPublisher.subscribe(it -> log.debug("received {}", it));
    }
}
