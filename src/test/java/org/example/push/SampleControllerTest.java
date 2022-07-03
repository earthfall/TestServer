package org.example.push;

import org.example.push.entity.PushEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@WebFluxTest(SampleController.class)
public class SampleControllerTest {

    @Autowired
    private WebTestClient webClient;

    @MockBean
    private DeviceNotificationService deviceNotificationService;

    @Test
    public void testHello() {
        StepVerifier.create(
                        webClient.get()
                                .uri("/hello/world")
                                .accept(MediaType.APPLICATION_JSON)
                                .exchange()
                                .expectStatus().isOk()
                                .returnResult(String.class)
                                .getResponseBody()
                )
                .expectNext("Test world!")
                .verifyComplete();
    }

    @Test
    public void testSend() {
        webClient.post()
                .uri("/send")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk();

        verify(deviceNotificationService).send(any(PushEntity.class));
    }
}