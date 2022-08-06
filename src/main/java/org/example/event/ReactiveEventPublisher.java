package org.example.event;

import org.springframework.stereotype.Component;
import reactor.core.Disposable;
import reactor.core.publisher.Sinks;

import java.util.function.Consumer;

@Component
public class ReactiveEventPublisher {

    private final Sinks.Many<ReactiveEvent> eventSink = Sinks.many().multicast().onBackpressureBuffer(1, true);

    public void publishEvent(ReactiveEvent event) {
        eventSink.tryEmitNext(event);
    }

    public Disposable subscribe(Consumer<? super ReactiveEvent> consumer) {
        return eventSink.asFlux().subscribe(consumer);
    }
}
