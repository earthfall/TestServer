package org.example.transform;

import lombok.SneakyThrows;
import org.example.aop.annotations.TransformData;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class TransformDataParser {

    private TransformDataParser() {
    }

    @SuppressWarnings("unchecked")
    @SneakyThrows(Throwable.class)
    static Supplier<Transformable<?>> transformConstructor(Class<?> clazz) {
        var lookup = MethodHandles.lookup();

        MethodHandle mh = lookup.findConstructor(clazz, MethodType.methodType(void.class));
        return (Supplier<Transformable<?>>) LambdaMetafactory.metafactory(
                lookup,
                "get",
                MethodType.methodType(Supplier.class),
                mh.type().generic(),
                mh,
                mh.type()
            )
            .getTarget()
            .invokeExact();
    }

    @SneakyThrows(IllegalAccessException.class)
    static Set<TransformItem> transformDataInfo(Class<?> clazz) {
        var handle = MethodHandles.lookup();
        var privateLookup = MethodHandles.privateLookupIn(clazz, handle);

        return getAllFields(clazz).stream()
            .map(field -> transformItem(privateLookup, field))
            .collect(Collectors.toSet());
    }

    @SneakyThrows(IllegalAccessException.class)
    private static TransformItem transformItem(MethodHandles.Lookup privateLookup, Field field) {
        return new TransformItem(privateLookup.unreflectVarHandle(field), hasTransformDataAnnotation(field));
    }

    private static List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        result.addAll(List.of(clazz.getDeclaredFields()));
        return result;
    }

    private static boolean hasTransformDataAnnotation(Field field) {
        return field.isAnnotationPresent(TransformData.class);
    }
}
