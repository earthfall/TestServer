package org.example.transform;

import lombok.Builder;
import lombok.Data;
import org.example.aop.annotations.TransformData;

@Data
@Builder(toBuilder = true)
public class TestModel2 implements Transformable<TestModel2> {
    private int val1;
    @TransformData
    private String val2;
    @TransformData
    private String val3;

    public TestModel2 deepCopy() {
        return toBuilder().build();
    }
}
