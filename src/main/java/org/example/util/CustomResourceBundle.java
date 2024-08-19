package org.example.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.ResourceBundle;

class CustomResourceBundle extends ResourceBundle {

    private final Map<String, String> resources;
    private final Enumeration<String> keyEnumeration;

    @SuppressWarnings("unchecked")
    @SneakyThrows(IOException.class)
    CustomResourceBundle(InputStream is) {
        var objectMapper = new ObjectMapper();
        resources = objectMapper.readValue(is, Map.class);
        keyEnumeration = Collections.enumeration(resources.keySet());
    }

    @Override
    protected String handleGetObject(String key) {
        return resources.get(key);
    }

    @Override
    public Enumeration<String> getKeys() {
        return keyEnumeration;
    }
}
