package org.example.transform;

import org.example.config.Transformable;
import org.springframework.stereotype.Service;

@Service
public class TransformService {

    public <T extends Transformable<T>> T transform(T source) throws IllegalAccessException {
        return source.transformData();
    }
}
