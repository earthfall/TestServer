package org.example.transform;

import java.lang.invoke.VarHandle;

public record TransformItem(
    VarHandle varHandle,
    boolean transformDataAnnotation
) {
}
