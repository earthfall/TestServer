package org.example.security;

import org.example.security.model.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class SecurityControllerProtoIntTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoSpyBean
    private UserService userService;

    @Test
    void deniesTheSameUserForJsonAndProtobuf() throws Exception {
        doReturn(false).when(userService).hasPermission("world", UserService.PERMISSION);
        mockMvc.perform(get("/permission-valid/world").accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isForbidden());
        mockMvc.perform(get("/permission-valid/world").accept(MediaType.APPLICATION_PROTOBUF))
            .andExpect(status().isForbidden());
    }

    @Test
    public void testProto() throws Exception {
        var result = mockMvc.perform(get("/permission-valid/world").accept(MediaType.APPLICATION_PROTOBUF))
            .andExpect(status().isOk())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROTOBUF))
            .andReturn();

        assertThat(Message.parseFrom(result.getResponse().getContentAsByteArray()))
            .isEqualTo(Message.newBuilder().setName("This hello").build());
    }
}
