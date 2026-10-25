package org.example.transform;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.modulith.test.ApplicationModuleTest;

import static org.assertj.core.api.Assertions.assertThat;

@ApplicationModuleTest
class TransformModuleTest {

    @Autowired
    private TransformService transformService;

    @Test
    void transformsDataWithOnlyTheTransformModuleBootstrapped() {
        var source = TestModel.builder().val1(1).val2("value").build();

        var transformed = transformService.transform(source);

        assertThat(transformed).isEqualTo(TestModel.builder()
            .val1(1).val2("transformed value").build());
    }
}
