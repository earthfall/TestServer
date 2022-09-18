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
public class TestModel2 implements Transformable<TestModel2> {
    private int val1;
    @TransformData
    private String val2;
    @TransformData
    private String val3;
}
