package org.example.transform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.jspecify.annotations.Nullable;
import lombok.NoArgsConstructor;
import org.example.annotation.TransformData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestModel2 implements Transformable<TestModel2> {
    private int val1;
    @TransformData
    private @Nullable String val2;
    @TransformData
    private @Nullable String val3;
}
