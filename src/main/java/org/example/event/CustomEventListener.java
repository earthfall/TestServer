package org.example.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class CustomEventListener {

    @EventListener
    public void onCustomEvent(CustomEvent event) {
        log.debug("received {}", event);
    }
}
