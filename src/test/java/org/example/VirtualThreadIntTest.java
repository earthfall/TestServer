package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "management.server.port=0")
@Import(VirtualThreadIntTest.ThreadProbeController.class)
class VirtualThreadIntTest {

    @LocalServerPort
    private int port;

    @Test
    void handlesHttpRequestsOnVirtualThreads() {
        var client = RestClient.create("http://localhost:" + port);

        assertThat(client.get().uri("/test/virtual-thread").retrieve().body(Boolean.class)).isTrue();
        assertThat(client.get().uri("/hello/world").retrieve().body(String.class)).isEqualTo("Test world!");
    }

    @RestController
    static class ThreadProbeController {
        @GetMapping("/test/virtual-thread")
        boolean isVirtualThread() {
            return Thread.currentThread().isVirtual();
        }
    }
}
