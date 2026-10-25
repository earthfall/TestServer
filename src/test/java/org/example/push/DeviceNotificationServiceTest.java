package org.example.push;

import org.example.push.entity.PushEntity;
import org.example.push.entity.PushDataEntity;
import org.example.push.entity.PushNotificationEntity;
import org.jspecify.annotations.Nullable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.support.RestClientHttpServiceGroupConfigurer;

import java.net.SocketTimeoutException;

import static java.util.Objects.requireNonNull;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Import(DeviceNotificationServiceTest.ClientTestConfiguration.class)
class DeviceNotificationServiceTest {
    private static final String ENDPOINT = "http://localhost/v1/projects/test-project/messages:send";

    @Autowired
    private DeviceNotificationService service;
    @Autowired
    private ClientMockServer mockServer;
    @MockitoBean
    private FirebaseAccessTokenProvider tokenProvider;
    private MockRestServiceServer server;

    @BeforeEach
    void resetServer() {
        server = requireNonNull(mockServer.server);
        server.reset();
        when(tokenProvider.accessToken()).thenReturn("test-token");
    }

    @Test
    void sendsV1MessageAndReadsMessageId() {
        server.expect(requestTo(ENDPOINT))
            .andExpect(method(HttpMethod.POST))
            .andExpect(header("Authorization", "Bearer test-token"))
            .andExpect(content().contentType(MediaType.APPLICATION_JSON))
            .andExpect(jsonPath("$.message.token").value("device"))
            .andExpect(jsonPath("$.message.topic").doesNotExist())
            .andExpect(jsonPath("$.message.notification.title").value("title"))
            .andExpect(jsonPath("$.message.data.entry").value("value"))
            .andExpect(jsonPath("$.message.android.priority").value("HIGH"))
            .andRespond(withSuccess("{\"name\":\"projects/test-project/messages/123\"}", MediaType.APPLICATION_JSON));

        var result = service.send(PushEntity.builder().to("device").priority("high")
            .notification(new PushNotificationEntity("title", "message"))
            .data(new PushDataEntity("value")).build());

        assertThat(result.name()).isEqualTo("projects/test-project/messages/123");
        server.verify();
    }

    @Test
    void sendsTopicsWithoutTokenOrOptionalNullFields() {
        server.expect(requestTo(ENDPOINT))
            .andExpect(jsonPath("$.message.topic").value("updates"))
            .andExpect(jsonPath("$.message.token").doesNotExist())
            .andExpect(jsonPath("$.message.android").doesNotExist())
            .andRespond(withSuccess("{\"name\":\"message-id\"}", MediaType.APPLICATION_JSON));
        service.send(PushEntity.builder().to("/topics/updates").build());
        server.verify();
    }

    @Test
    void rejectsEmptyResponse() {
        server.expect(requestTo(ENDPOINT)).andRespond(withNoContent());
        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(RestClientException.class).hasMessageContaining("empty or invalid");
        server.verify();
    }

    @Test
    void rejectsResponseWithoutMessageId() {
        server.expect(requestTo(ENDPOINT)).andRespond(withSuccess("{}", MediaType.APPLICATION_JSON));
        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(RestClientException.class);
        server.verify();
    }

    @Test
    void propagatesAuthenticationFailure() {
        server.expect(requestTo(ENDPOINT)).andRespond(withUnauthorizedRequest());
        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(HttpClientErrorException.Unauthorized.class);
        server.verify();
    }

    @Test
    void propagatesRemoteServerErrors() {
        server.expect(requestTo(ENDPOINT)).andRespond(withServerError());
        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }

    @Test
    void propagatesTimeoutWithoutRetryingSend() {
        server.expect(requestTo(ENDPOINT)).andRespond(withException(new SocketTimeoutException("timeout")));
        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(ResourceAccessException.class);
        server.verify();
    }

    static class ClientMockServer {
        private @Nullable MockRestServiceServer server;
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
