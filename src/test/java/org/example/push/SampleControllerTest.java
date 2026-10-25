package org.example.push;

import org.example.config.AppProperties;
import org.example.push.entity.PushEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleController.class)
@EnableConfigurationProperties(AppProperties.class)
public class SampleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DeviceNotificationService deviceNotificationService;

    @Test
    public void testHello() throws Exception {
        mockMvc.perform(get("/hello/world").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Test world!"));
    }

    @Test
    public void testSend() throws Exception {
        when(deviceNotificationService.send(any(PushEntity.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));

        mockMvc.perform(post("/send").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.to").value("/server/send"))
            .andExpect(jsonPath("$.notification.title").value("humanpoc notification"));

        verify(deviceNotificationService).send(any(PushEntity.class));
    }
}
