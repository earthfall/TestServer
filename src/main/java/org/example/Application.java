package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan("org.example.config")
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
