package org.example.util;

import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.ResourceBundle;

class CustomResourceBundle extends ResourceBundle {
    private static final JsonMapper MAPPER = JsonMapper.builder().build();
    private final Map<String, String> resources;

    CustomResourceBundle(InputStream stream) {
        var root = MAPPER.readTree(stream);
        if (root == null || !root.isObject()) {
            throw new IllegalArgumentException("Resource bundle must be a JSON object");
        }
        var values = new LinkedHashMap<String, String>();
        for (var property : root.properties()) {
            if (!property.getValue().isString()) {
                throw new IllegalArgumentException("Resource bundle values must be strings: " + property.getKey());
            }
            values.put(property.getKey(), property.getValue().asString());
        }
        resources = Map.copyOf(values);
    }

    @Override
    protected @Nullable String handleGetObject(String key) {
        return resources.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        var keys = new LinkedHashSet<>(resources.keySet());
        var parentBundle = parent;
        if (parentBundle != null) {
            keys.addAll(Collections.list(parentBundle.getKeys()));
        }
        return Collections.enumeration(keys);
    }
}
