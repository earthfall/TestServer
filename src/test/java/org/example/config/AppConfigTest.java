package org.example.config;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTestContextBootstrapper;
import org.springframework.test.context.BootstrapWith;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@BootstrapWith(SpringBootTestContextBootstrapper.class)
public class AppConfigTest {

    @Autowired
    private AppConfig appConfig;

    @Test
    public void getId() throws Exception {
        assertThat(appConfig.getId(), is(1));
    }

    @Test
    public void getGreeting() throws Exception {
        assertThat(appConfig.getGreeting(), is("Hello"));
    }

}