package org.example.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    public void getId() {
        assertThat(appConfig.getId()).isEqualTo(2);
    }

    @Test
    public void getGreeting() {
        assertThat(appConfig.getGreeting()).isEqualTo("Test");
    }
}