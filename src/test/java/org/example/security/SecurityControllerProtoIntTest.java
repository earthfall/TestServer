package org.example.security;

import org.example.security.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
@AutoConfigureWebTestClient
public class SecurityControllerProtoIntTest {

    @Autowired
    private WebTestClient webClient;

    @Test
    public void testProto() {
        webClient.get()
            .uri("/permission-valid/world")
            .accept(MediaType.APPLICATION_PROTOBUF)
            .exchange()
            .expectStatus().isOk()
            .expectBody(Message.class)
            .isEqualTo(
                Message.newBuilder().setName("This hello").build()
            );
    }
}