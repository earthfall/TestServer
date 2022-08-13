package org.example.config;

import org.example.aop.annotations.TransformData;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public interface Transformable<T> {

    T deepCopy();

    default T transformData() throws IllegalAccessException {
        var target = deepCopy();

        for (Field field : getAllFields(target.getClass())) {
            field.setAccessible(true);

            var type = field.getType();
            var object = field.get(target);
            if (type == String.class) {
                if (hasTransformDataAnnotation(field)) {
                    field.set(target, transformString((String) object));
                }
            } else if (Transformable.class.isAssignableFrom(object.getClass())) {
                field.set(target, ((Transformable<?>) object).transformData());
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
