package org.example.config;

import org.example.aop.annotations.TransformData;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Transformable<T> {

    T deepCopy();

    default T transformData() throws IllegalAccessException {
        var handle = MethodHandles.lookup();

        var target = deepCopy();

        var privateLookup = MethodHandles.privateLookupIn(target.getClass(), handle);

        for (Field field : getAllFields(target.getClass())) {
            var varHandle = privateLookup.unreflectVarHandle(field);

            var object = varHandle.get(target);
            if (object instanceof String && hasTransformDataAnnotation(field)) {
                varHandle.set(target, transformString((String) object));
            } else if (object instanceof Transformable) {
                varHandle.set(target, ((Transformable<?>) object).transformData());
            }
        }

        return target;
    }

    static String transformString(String value) {
        return "transformed " + value;
    }

    private List<Field> getAllFields(Class<?> clazz) {
        if (clazz == null) {
            return Collections.emptyList();
        }

        List<Field> result = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        result.addAll(List.of(clazz.getDeclaredFields()));
        return result;
    }

    private boolean hasTransformDataAnnotation(Field field) {
        return field.isAnnotationPresent(TransformData.class);
    }
}
