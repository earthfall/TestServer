package org.example.util;

import org.jspecify.annotations.Nullable;
import tools.jackson.databind.json.JsonMapper;

import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

class CustomResourceBundle extends ResourceBundle {

    private final Map<String, @Nullable String> resources;
    private final Enumeration<String> keyEnumeration;

    @SuppressWarnings("unchecked")
    CustomResourceBundle(InputStream is) {
        var objectMapper = JsonMapper.builder().build();
        resources = objectMapper.readValue(is, Map.class);
        keyEnumeration = Collections.enumeration(resources.keySet());
    }

    @Override
    protected @Nullable String handleGetObject(String key) {
        return resources.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return keyEnumeration;
    }
}
