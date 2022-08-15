package org.example.transform;

import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@Service
public class TransformService {

    private final Map<Class<?>, Set<TransformItem>> classCache = new ConcurrentHashMap<>();

    public <T extends Transformable<T>> T transform(T source) throws IllegalAccessException {
        return transformData(source.deepCopy());
    }

    private <T extends Transformable<T>> T transformData(T source) throws IllegalAccessException {
        var transforms = classCache.computeIfAbsent(source.getClass(), key -> {
            var infos = Transformable.transformDataInfo(key);
            log.info("Transform initialized for {}", key);
            return infos;
        });
        return transformData(source, transforms);
    }

    private <T extends Transformable<T>> T transformData(T source, Set<TransformItem> transforms) throws IllegalAccessException {
        for (TransformItem item : transforms) {
            var varHandle = item.getVarHandle();
            var object = varHandle.get(source);

            if (object instanceof String && item.isTransformDataAnnotation()) {
                varHandle.set(source, Transformable.transformString((String) object));
            } else if (object instanceof Transformable) {
                varHandle.set(source, transformData((Transformable) object));
            }
        }

        return source;
    }
}
