package org.example.transform;

import java.util.Set;
import java.util.function.Supplier;

public record TransformTarget(
    Supplier<Transformable<?>> creator,
    Set<TransformItem> items
) {
}
