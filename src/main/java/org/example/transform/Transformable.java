package org.example.transform;

import org.jspecify.annotations.Nullable;
import lombok.SneakyThrows;

import java.util.function.Function;

// Transformable should declare default constructor
public interface Transformable<T extends Transformable<T>> {

    default T transformedObject(
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer
    ) {
        return transformedObject(typeProvider, transformer, typeProvider.apply(getClass()));
    }

    @SneakyThrows(Throwable.class)
    default T transformedObject(
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer,
        TransformTarget transforms
    ) {
        Transformable<?> instance = transforms.creator().get();

        for (TransformItem item : transforms.items()) {
            var varHandle = item.varHandle();
            varHandle.set(instance, transformItem(item, varHandle.get(this), typeProvider, transformer));
        }

        return (T) instance;
    }

    private @Nullable Object transformItem(
        TransformItem item,
        @Nullable Object field,
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer
    ) {
        if (field instanceof String strObject && item.transformData()) {
            return transformer.apply(strObject);
        } else if (field instanceof Transformable<?> transformableObject) {
            return transformableObject.transformedObject(typeProvider, transformer);
        } else {
            return field;
        }
    }
}
