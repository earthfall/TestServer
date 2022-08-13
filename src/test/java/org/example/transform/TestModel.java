package org.example.transform;

import lombok.Builder;
import lombok.Data;
import org.example.aop.annotations.TransformData;
import org.example.config.Transformable;

@Data
@Builder(toBuilder = true)
public class TestModel implements Transformable<TestModel> {
    private int val1;
    @TransformData
    private String val2;
    private Inner val3;

    public TestModel deepCopy() {
        return toBuilder().build();
    }

    @Data
    @Builder(toBuilder = true)
    static class Inner implements Transformable<Inner> {
        @TransformData
        private String val4;
        private long val5;

        @Override
        public Inner deepCopy() {
            return toBuilder().build();
        }
    }
}
