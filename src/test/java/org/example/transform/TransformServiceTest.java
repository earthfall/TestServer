package org.example.transform;

import org.example.config.Transformable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class TransformServiceTest {

    @Test
    public void testTransform() throws IllegalAccessException {
        TestModel model = TestModel.builder()
            .val1(1)
            .val2("2")
            .val3(
                TestModel.Inner.builder()
                    .val4("4")
                    .val5(5)
                    .build()
            )
            .build();

        var expect = TestModel.builder()
            .val1(1)
            .val2(Transformable.transformString("2"))
            .val3(
                TestModel.Inner.builder()
                    .val4(Transformable.transformString("4"))
                    .val5(5)
                    .build()
            )
            .build();

        var result = new TransformService().transform(model);

        assertThat(result).isEqualTo(expect);
    }
}
