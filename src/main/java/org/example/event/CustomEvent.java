package org.example.event;

import lombok.Value;

@Value
public class CustomEvent implements ReactiveEvent {
    String message;
}
