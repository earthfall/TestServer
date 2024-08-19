package org.example.util;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

class CustomResourceBundleControl extends ResourceBundle.Control {

    @Override
    public List<String> getFormats(String baseName) {
        if (baseName == null) {
            throw new IllegalArgumentException("No basename specified");
        }
        return List.of("json");
    }

    @Override
    public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload) throws IllegalAccessException, InstantiationException, IOException {
        if (baseName == null || locale == null || format == null || loader == null) {
            throw new IllegalArgumentException("Wrong bundle parameter");
        }

        if (!format.equals("json")) {
            return null;
        }

        var bundleName = toBundleName(baseName, locale);
        var resourceName = toResourceName(bundleName, format);
        var url = loader.getResource(resourceName);
        if (url == null) {
            return null;
        }

        var connection = url.openConnection();
        if (connection == null) {
            return null;
        }

        if (reload) {
            // disable caches if reloading
            connection.setUseCaches(false);
        }

        try (var stream = connection.getInputStream()) {
            if (stream != null) {
                return new CustomResourceBundle(new BufferedInputStream(stream));
            }
        }

        return null;
    }
}
