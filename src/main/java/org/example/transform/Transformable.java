package org.example.transform;

import org.jspecify.annotations.Nullable;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Function;

// Mutable POJOs with a no-argument constructor; collections remain shallow copies.
public interface Transformable<T extends Transformable<T>> {
    default T transformedObject(
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer
    ) {
        return transformedObject(typeProvider, transformer, typeProvider.apply(getClass()));
    }

    @SuppressWarnings("unchecked")
    default T transformedObject(
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer,
        TransformTarget transforms
    ) {
        return (T) copy(typeProvider, transformer, transforms, new IdentityHashMap<>());
    }

    private Transformable<?> copy(
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer,
        TransformTarget transforms,
        Map<Transformable<?>, Transformable<?>> copies
    ) {
        var existing = copies.get(this);
        if (existing != null) {
            return existing;
        }
        var instance = transforms.creator().get();
        copies.put(this, instance);
        for (var item : transforms.items()) {
            var handle = item.varHandle();
            handle.set(instance, transformItem(item, handle.get(this), typeProvider, transformer, copies));
        }
        return instance;
    }

    private @Nullable Object transformItem(
        TransformItem item,
        @Nullable Object field,
        Function<Class<?>, TransformTarget> typeProvider,
        Function<String, String> transformer,
        Map<Transformable<?>, Transformable<?>> copies
    ) {
        if (field instanceof String value && item.transformData()) {
            return transformer.apply(value);
        }
        if (field instanceof Transformable<?> value) {
            return value.copy(typeProvider, transformer, typeProvider.apply(value.getClass()), copies);
        }
        return field;
    }
}
