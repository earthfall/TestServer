package org.example.transform;

import java.util.Set;
import java.util.function.Function;

public interface Transformable<T extends Transformable<T>> {

    T deepCopy();

    default T transformData(Function<Class<?>, Set<TransformItem>> typeProvider, Function<String, String> transformer) throws IllegalAccessException {
        return transformData(typeProvider, transformer, typeProvider.apply(getClass()));
    }

    private T transformData(
        Function<Class<?>, Set<TransformItem>> typeProvider, Function<String, String> transformer,
        Set<TransformItem> transforms
    ) throws IllegalAccessException {
        for (TransformItem item : transforms) {
            var varHandle = item.getVarHandle();
            var object = varHandle.get(this);

            if (object instanceof String && item.isTransformDataAnnotation()) {
                varHandle.set(this, transformer.apply((String) object));
            } else if (object instanceof Transformable) {
                varHandle.set(this, ((Transformable<?>) object).transformData(typeProvider, transformer));
            }
        }

        return (T) this;
    }
}
