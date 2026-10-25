package org.example.transform;

import org.example.annotation.TransformData;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TransformBoundaryTest {
    private final TransformService service = new TransformService(new Transformer());

    @Test
    void transformsInheritedPrivateFieldsAndIgnoresStaticFields() {
        var source = new Child();
        var result = service.transform(source);
        assertThat(((Parent) result).inherited).isEqualTo("transformed parent");
        assertThat(result.value).isEqualTo("transformed child");
        assertThat(Child.shared).isEqualTo("static");
        assertThat(((Parent) source).inherited).isEqualTo("parent");
    }

    @Test
    void preservesNullFieldsAndPrimitiveValues() {
        var result = service.transform(new Node());
        assertThat(result.value).isNull();
        assertThat(result.left).isNull();
        assertThat(result.count).isEqualTo(7);
    }

    @Test
    void preservesCyclesAndSharedReferencesWithoutMutatingSource() {
        var source = new Node();
        var nested = new Node();
        source.value = "root";
        source.left = nested;
        source.right = nested;
        nested.left = source;
        var result = service.transform(source);
        var copiedNested = java.util.Objects.requireNonNull(result.left);
        assertThat(result).isNotSameAs(source);
        assertThat(result.value).isEqualTo("transformed root");
        assertThat(result.left).isSameAs(result.right).isNotSameAs(nested);
        assertThat(copiedNested.left).isSameAs(result);
        assertThat(source.value).isEqualTo("root");
    }

    @Test
    void rejectsFinalInstanceFieldsWithAUsefulMessage() {
        assertThatThrownBy(() -> service.transform(new FinalFields()))
            .isInstanceOf(TransformException.class).hasMessageContaining("Final instance field");
    }

    @Test
    void rejectsTypesWithoutNoArgumentConstructors() {
        assertThatThrownBy(() -> service.transform(new NoDefault("value")))
            .isInstanceOf(TransformException.class).hasMessageContaining("no-argument constructor");
    }

    static class Parent {
        @TransformData
        private String inherited = "parent";
    }

    static class Child extends Parent implements Transformable<Child> {
        @TransformData
        static String shared = "static";
        @TransformData
        String value = "child";
    }

    static class Node implements Transformable<Node> {
        @TransformData
        @Nullable String value;
        @Nullable Node left;
        @Nullable Node right;
        int count = 7;
    }

    static class FinalFields implements Transformable<FinalFields> {
        final String value = "immutable";
    }

    static class NoDefault implements Transformable<NoDefault> {
        String value;
        NoDefault(String value) { this.value = value; }
    }
}
