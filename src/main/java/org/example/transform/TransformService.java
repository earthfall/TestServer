package org.example.transform;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@RequiredArgsConstructor
@Service
public class TransformService {

    private final Map<Class<?>, TransformTarget> classCache = new ConcurrentHashMap<>();

    private final Transformer transformer;

    public <T extends Transformable<T>> T transform(T source) {
        return source.transformedObject(
            clazz -> classCache.computeIfAbsent(clazz, key -> {
                var target = new TransformTarget(
                    TransformDataParser.transformConstructor(key),
                    TransformDataParser.transformDataInfo(key)
                );

                log.info("Transform initialized for {}", key);
                return target;
            }),
            transformer::transformString
        );
    }
}
