package org.example.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    public void getId() {
        assertThat(appConfig.getId(), is(2));
    }

    @Test
    public void getGreeting() {
        assertThat(appConfig.getGreeting(), is("Test"));
    }

}