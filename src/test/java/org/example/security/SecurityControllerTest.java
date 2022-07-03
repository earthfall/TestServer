package org.example.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

@WebFluxTest(SecurityController.class)
@Import({SecurityService.class, SecurityEvaluator.class, UserService.class})
public class SecurityControllerTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testEvaluator() {
        StepVerifier.create(
                webClient.get()
                    .uri("/permission-valid/world")
                    .accept(MediaType.APPLICATION_JSON)
                    .exchange()
                    .expectStatus().isOk()
                    .returnResult(String.class)
                    .getResponseBody()
            )
            .expectNext("This hello")
            .verifyComplete();
    }

    @Test
    public void testEvaluatorForbidden() {
        StepVerifier.create(
                webClient.get()
                    .uri("/permission/world")
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