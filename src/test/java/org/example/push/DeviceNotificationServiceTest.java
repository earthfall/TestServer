package org.example.push;

import org.example.push.entity.PushEntity;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DeviceNotificationServiceTest.ClientTestConfiguration.class)
class DeviceNotificationServiceTest {

    @Autowired
    private DeviceNotificationService service;

    @Autowired
    private ClientMockServer mockServer;

    private MockRestServiceServer server;

    @BeforeEach
    void resetServer() {
        server = mockServer.server;
        server.reset();
    }

    @Test
    void sendsNotificationAndReadsResponse() {
        server.expect(requestTo("http://localhost/send"))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", "key=test-key"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.to").value("device"))
            .andRespond(withSuccess("""
                {"to":"device","priority":"high",
                 "notification":{"title":"title","body":"message"},"data":{"entry":"value"}}
                """, MediaType.APPLICATION_JSON));

        var result = service.send(PushEntity.builder().to("device").priority("high").build());

        assertThat(result.to()).isEqualTo("device");
        assertThat(result.priority()).isEqualTo("high");
        assertThat(result.notification().title()).isEqualTo("title");
        assertThat(result.notification().body()).isEqualTo("message");
        assertThat(result.data().entry()).isEqualTo("value");
        server.verify();
    }

    @Test
    void propagatesRemoteServerErrors() {
        server.expect(requestTo("http://localhost/send")).andRespond(withServerError());

        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }

    static class ClientMockServer {
        private MockRestServiceServer server;
    }

    @TestConfiguration(proxyBeanMethods = false)
    static class ClientTestConfiguration {
        @Bean
        ClientMockServer clientMockServer() {
            return new ClientMockServer();
        }

        @Bean
        RestClientHttpServiceGroupConfigurer mockServiceConfigurer(ClientMockServer mockServer) {
            return groups -> groups.forEachClient((group, builder) -> {
                if (group.name().equals("firebase")) {
                    mockServer.server = MockRestServiceServer.bindTo(builder).build();
                }
            });
        }
    }
}
