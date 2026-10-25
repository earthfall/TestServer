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
public class TestModel implements Transformable<TestModel> {
    private int val1;
    @TransformData
    private @Nullable String val2;
    private @Nullable Inner val3;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    static class Inner implements Transformable<Inner> {
        @TransformData
        private @Nullable String val4;
        private long val5;
    }
}
