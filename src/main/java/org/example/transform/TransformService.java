package org.example.transform;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Log4j2
@RequiredArgsConstructor
@Service
public class TransformService {

    private final Map<Class<?>, Set<TransformItem>> classCache = new ConcurrentHashMap<>();

    private final Transformer transformer;

    public <T extends Transformable<T>> T transform(T source) throws IllegalAccessException {
        return source.deepCopy()
            .transformData(
                clazz -> classCache.computeIfAbsent(clazz, key -> {
                    var infos = TransformDataParser.transformDataInfo(key);
                    log.info("Transform initialized for {}", key);
                    return infos;
                }),
                transformer::transformString
            );
    }
}
