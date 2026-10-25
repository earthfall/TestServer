package org.example.transform;

import org.example.annotation.TransformData;
import org.jspecify.annotations.Nullable;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

public class TransformDataParser {
    private TransformDataParser() {
    }

    static Supplier<Transformable<?>> transformConstructor(Class<?> clazz) {
        try {
            var lookup = MethodHandles.privateLookupIn(clazz, MethodHandles.lookup());
            var constructor = lookup.findConstructor(clazz, MethodType.methodType(void.class));
            return () -> {
                try {
                    return (Transformable<?>) constructor.invoke();
                } catch (Error error) {
                    throw error;
                } catch (Throwable exception) {
                    throw new TransformException("Cannot construct " + clazz.getName(), exception);
                }
            };
        } catch (ReflectiveOperationException exception) {
            throw new TransformException("Transformable type requires an accessible no-argument constructor: "
                + clazz.getName(), exception);
        }
    }

    static Set<TransformItem> transformDataInfo(Class<?> clazz) {
        var items = new LinkedHashSet<TransformItem>();
        for (var field : getAllFields(clazz)) {
            if (Modifier.isStatic(field.getModifiers()) || field.isSynthetic()) {
                continue;
            }
            if (Modifier.isFinal(field.getModifiers())) {
                throw new TransformException("Final instance field is not supported: "
                    + field.getDeclaringClass().getName() + "." + field.getName());
            }
            try {
                var lookup = MethodHandles.privateLookupIn(field.getDeclaringClass(), MethodHandles.lookup());
                items.add(new TransformItem(lookup.unreflectVarHandle(field),
                    field.isAnnotationPresent(TransformData.class)));
            } catch (IllegalAccessException exception) {
                throw new TransformException("Cannot access field " + field, exception);
            }
        }
        return Set.copyOf(items);
    }

    private static List<Field> getAllFields(@Nullable Class<?> clazz) {
        if (clazz == null || clazz == Object.class) {
            return List.of();
        }
        var fields = new ArrayList<>(getAllFields(clazz.getSuperclass()));
        fields.addAll(List.of(clazz.getDeclaredFields()));
        return fields;
    }
}
