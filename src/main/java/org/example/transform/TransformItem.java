package org.example.transform;

import lombok.Value;

import java.lang.invoke.VarHandle;

@Value
public class TransformItem {
    VarHandle varHandle;
    boolean transformDataAnnotation;
}
