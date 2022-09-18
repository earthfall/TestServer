package org.example.event;

public record CustomEvent(
    String message
) implements ReactiveEvent {
}
