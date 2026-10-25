package org.example.push;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.service.registry.ImportHttpServices;

@Configuration(proxyBeanMethods = false)
@ImportHttpServices(group = "firebase", types = FirebaseClient.class)
public class FirebaseClientConfiguration {
}
