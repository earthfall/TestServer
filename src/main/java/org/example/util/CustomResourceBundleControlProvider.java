package org.example.util;

import java.util.ResourceBundle;
import java.util.spi.ResourceBundleControlProvider;

public class CustomResourceBundleControlProvider implements ResourceBundleControlProvider {
    private static final ResourceBundle.Control JSON_CONTROL = new CustomResourceBundleControl();

    @Override
    public ResourceBundle.Control getControl(String baseName) {
        if (baseName.startsWith("json")) {
            return JSON_CONTROL;
        }
        return null;
    }
}
