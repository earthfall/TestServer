package org.example.exception;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.net.SocketTimeoutException;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionAdviceTest {
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new ProbeController())
        .setControllerAdvice(new GlobalExceptionAdvice()).build();

    @Test
    void preservesFrameworkBadRequestForTypeMismatch() throws Exception {
        mvc.perform(get("/probe/number/not-a-number"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void preservesFrameworkValidationErrors() throws Exception {
        mvc.perform(post("/probe/input").contentType(MediaType.APPLICATION_JSON).content("{\"value\":\"\"}"))
            .andExpect(status().isBadRequest()).andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void returnsForbiddenProblem() throws Exception {
        mvc.perform(get("/probe/forbidden"))
            .andExpect(status().isForbidden())
            .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
            .andExpect(jsonPath("$.detail").value("Permission denied"));
    }

    @Test
    void hidesUnexpectedExceptionDetails() throws Exception {
        mvc.perform(get("/probe/unexpected")).andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.detail").value("Internal server error"))
            .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void mapsUpstreamAuthenticationFailureToBadGateway() throws Exception {
        mvc.perform(get("/probe/upstream")).andExpect(status().isBadGateway())
            .andExpect(content().string(not(containsString("secret"))));
    }

    @Test
    void mapsTimeoutToGatewayTimeout() throws Exception {
        mvc.perform(get("/probe/timeout")).andExpect(status().isGatewayTimeout())
            .andExpect(jsonPath("$.status").value(504));
    }

    @Test
    void mapsInvalidArgumentsToBadRequest() throws Exception {
        mvc.perform(get("/probe/invalid")).andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.detail").value("Invalid request input"));
    }

    @RestController
    static class ProbeController {
        @GetMapping("/probe/number/{value}")
        int number(@PathVariable int value) { return value; }
        @PostMapping("/probe/input")
        String input(@Valid @RequestBody Input input) { return input.value(); }
        @GetMapping("/probe/forbidden")
        String forbidden() { throw new NoPermissionException("Permission denied"); }
        @GetMapping("/probe/unexpected")
        String unexpected() { throw new IllegalStateException("secret internal detail"); }
        @GetMapping("/probe/upstream")
        String upstream() { throw new HttpClientErrorException(HttpStatus.UNAUTHORIZED, "secret upstream detail"); }
        @GetMapping("/probe/timeout")
        String timeout() { throw new ResourceAccessException("secret endpoint", new SocketTimeoutException()); }
        @GetMapping("/probe/invalid")
        String invalid() { throw new IllegalArgumentException("secret value"); }
    }

    record Input(@NotBlank String value) {
    }
}
