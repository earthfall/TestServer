package org.example.push;

import org.example.config.FirebaseProperties;
import org.example.push.entity.PushEntity;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

class DeviceNotificationServiceTest {

    @Test
    void sendsNotificationAndReadsResponse() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new DeviceNotificationService(new FirebaseProperties("test-key", "http://localhost/send"), builder);

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

        assertThat(result.getTo()).isEqualTo("device");
        assertThat(result.getPriority()).isEqualTo("high");
        assertThat(result.getNotification().getTitle()).isEqualTo("title");
        assertThat(result.getNotification().getBody()).isEqualTo("message");
        assertThat(result.getData().getEntry()).isEqualTo("value");
        server.verify();
    }

    @Test
    void propagatesRemoteServerErrors() {
        var builder = RestClient.builder();
        var server = MockRestServiceServer.bindTo(builder).build();
        var service = new DeviceNotificationService(new FirebaseProperties("test-key", "http://localhost/send"), builder);
        server.expect(requestTo("http://localhost/send")).andRespond(withServerError());

        assertThatThrownBy(() -> service.send(PushEntity.builder().to("device").build()))
            .isInstanceOf(HttpServerErrorException.class);
        server.verify();
    }
}
