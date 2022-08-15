package org.example.transform;

import org.example.push.DeviceNotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(DeviceNotificationService.class)
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

    @Test
    public void testTransformMultiple() throws IllegalAccessException {
        TestModel model = TestModel.builder()
            .val1(1)
            .val2("2")
            .build();

        TestModel2 model2 = TestModel2.builder()
            .val1(1)
            .val2("2")
            .val3("3")
            .build();

        TestModel2 model3 = TestModel2.builder()
            .val1(11)
            .val2("12")
            .val3("13")
            .build();

        var expect = TestModel.builder()
            .val1(1)
            .val2(Transformable.transformString("2"))
            .build();

        var expect2 = TestModel2.builder()
            .val1(1)
            .val2(Transformable.transformString("2"))
            .val3(Transformable.transformString("3"))
            .build();

        var expect3 = TestModel2.builder()
            .val1(11)
            .val2(Transformable.transformString("12"))
            .val3(Transformable.transformString("13"))
            .build();

        var transformService = new TransformService();
        var result = transformService.transform(model);
        var result2 = transformService.transform(model2);
        var result3 = transformService.transform(model3);

        assertThat(result).isEqualTo(expect);
        assertThat(result2).isEqualTo(expect2);
        assertThat(result3).isEqualTo(expect3);
    }
}
