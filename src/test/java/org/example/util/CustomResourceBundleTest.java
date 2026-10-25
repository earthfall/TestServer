package org.example.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CustomResourceBundleTest {
    @Test
    void returnsFreshKeysOnEveryCall() {
        var bundle = bundle("{\"hello\":\"안녕하세요\",\"empty\":\"\"}");
        assertThat(Collections.list(bundle.getKeys())).containsExactlyInAnyOrder("hello", "empty");
        assertThat(Collections.list(bundle.getKeys())).containsExactlyInAnyOrder("hello", "empty");
        assertThat(bundle.getString("hello")).isEqualTo("안녕하세요");
        assertThat(bundle.getString("empty")).isEmpty();
        assertThatThrownBy(() -> bundle.getString("missing")).isInstanceOf(MissingResourceException.class);
    }

    @Test
    void includesParentKeysOnceAndPrefersLocalValue() {
        var parent = bundle("{\"shared\":\"parent\",\"parentOnly\":\"fallback\"}");
        var child = bundle("{\"shared\":\"child\",\"childOnly\":\"local\"}");
        child.attachParent(parent);
        assertThat(Collections.list(child.getKeys())).containsExactlyInAnyOrder("shared", "parentOnly", "childOnly");
        assertThat(child.keySet()).containsExactlyInAnyOrder("shared", "parentOnly", "childOnly");
        assertThat(child.getString("shared")).isEqualTo("child");
        assertThat(child.getString("parentOnly")).isEqualTo("fallback");
    }

    @ParameterizedTest
    @ValueSource(strings = {"null", "[]", "42", "{\"key\":null}", "{\"key\":2}", "{\"key\":{}}", "{\"key\":[]}"})
    void rejectsNonStringResourceDocuments(String json) {
        assertThatThrownBy(() -> bundle(json)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void rejectsMalformedJson() {
        assertThatThrownBy(() -> bundle("{")).isInstanceOf(RuntimeException.class);
    }

    private static TestBundle bundle(String json) {
        return new TestBundle(json);
    }

    private static class TestBundle extends CustomResourceBundle {
        TestBundle(String json) {
            super(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        }

        void attachParent(ResourceBundle parent) {
            setParent(parent);
        }
    }
}
