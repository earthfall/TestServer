package org.example.transform;

import lombok.Builder;
import lombok.Data;
import org.example.aop.annotations.TransformData;
import org.example.config.Transformable;

@Data
@Builder
public class TestModel implements Transformable<TestModel> {
    private int val1;
    @TransformData
    private String val2;
    private Inner val3;

    @Override
    public TestModel deepCopy() {
        try {
            return (TestModel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError(e);
        }
    }

    @Data
    @Builder
    static class Inner implements Transformable<Inner> {
        @TransformData
        private String val4;
        private long val5;

        @Override
        public Inner deepCopy() {
            try {
                return (Inner) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError(e);
            }
        }
    }
}
