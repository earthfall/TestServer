package org.example;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.Objects.requireNonNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OpenApiIntTest {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void generatesSpecificationFromControllersAndRequestModels() throws Exception {
        var json = mockMvc.perform(get("/v3/api-docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.openapi").exists())
            .andExpect(jsonPath("$.paths['/hello/{name}'].get").exists())
            .andExpect(jsonPath("$.paths['/send'].post.requestBody").exists())
            .andExpect(jsonPath("$.paths['/permission-valid/{name}'].get").exists())
            .andExpect(jsonPath("$.paths['/permission/{name}'].get").exists())
            .andExpect(jsonPath("$.paths['/permission-aop/{name}'].get").exists())
            .andExpect(jsonPath("$.components.schemas.PushEntity.properties.to").exists())
            .andReturn().getResponse().getContentAsByteArray();
        var yaml = mockMvc.perform(get("/v3/api-docs.yaml"))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsByteArray();
        var directory = Path.of(requireNonNull(System.getProperty("openapi.outputDir")));
        Files.createDirectories(directory);
        Files.write(directory.resolve("openapi.json"), json);
        Files.write(directory.resolve("openapi.yaml"), yaml);
    }
}
