package org.example.transform;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.aop.annotations.TransformData;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class TestModel implements Transformable<TestModel> {
    private int val1;
    @TransformData
    private String val2;
    private Inner val3;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder(toBuilder = true)
    static class Inner implements Transformable<Inner> {
        @TransformData
        private String val4;
        private long val5;
    }
}
