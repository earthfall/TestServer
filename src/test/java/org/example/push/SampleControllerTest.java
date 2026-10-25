package org.example.push;

import org.example.config.AppProperties;
import org.example.push.entity.FirebaseSendResponse;
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
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(SampleController.class)
@EnableConfigurationProperties(AppProperties.class)
class SampleControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockitoBean
    private DeviceNotificationService deviceNotificationService;

    @Test
    void testHello() throws Exception {
        mockMvc.perform(get("/hello/world").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(content().string("Test world!"));
    }

    @Test
    void sendsValidatedInputAndReturnsMessageId() throws Exception {
        when(deviceNotificationService.send(any(PushEntity.class)))
            .thenReturn(new FirebaseSendResponse("projects/test-project/messages/123"));
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON)
                .content("{\"to\":\"device\",\"notification\":{\"title\":\"hello\",\"body\":\"world\"}}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("projects/test-project/messages/123"));
        verify(deviceNotificationService).send(any(PushEntity.class));
    }

    @Test
    void rejectsMissingDestinationBeforeCallingExternalService() throws Exception {
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isBadRequest())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.status").value(400));
        verifyNoInteractions(deviceNotificationService);
    }

    @Test
    void rejectsInvalidNestedInput() throws Exception {
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON)
                .content("{\"to\":\"device\",\"notification\":{\"title\":\"\",\"body\":\"body\"}}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(deviceNotificationService);
    }

    @Test
    void rejectsMalformedJson() throws Exception {
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON).content("{"))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void rejectsEmptyTopicAndInvalidPriority() throws Exception {
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON)
                .content("{\"to\":\"/topics/\"}"))
            .andExpect(status().isBadRequest());
        mockMvc.perform(post("/send").contentType(MediaType.APPLICATION_JSON)
                .content("{\"to\":\"device\",\"priority\":\"urgent\"}"))
            .andExpect(status().isBadRequest());
        verifyNoInteractions(deviceNotificationService);
    }
}
