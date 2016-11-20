package org.example.controller;

import org.example.config.AppConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SampleController {

    @Autowired
    private AppConfig appConfig;

    @RequestMapping("/hello/{name}")
    String hello(@PathVariable String name) {
        return appConfig.getGreeting() + ' ' + name + "!";
    }
}
