package org.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@SpringBootTest
@AutoConfigureWebTestClient
public class SecurityControllerIntTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testEvaluatorAop() {
        StepVerifier.create(
                webClient.get()
                    .uri("/permission-aop/world")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isForbidden()
                    .returnResult(String.class)
                    .getResponseBody()
            )
            .expectNext("world does not have permission: test1")
            .verifyComplete();
    }
}