package org.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AppPropertiesTest {

    @Autowired
    private AppProperties appProperties;

    @Test
    public void getId() {
        assertThat(appProperties.id()).isEqualTo(2);
    }

    @Test
    public void getGreeting() {
        assertThat(appProperties.greeting()).isEqualTo("Test");
    }
}