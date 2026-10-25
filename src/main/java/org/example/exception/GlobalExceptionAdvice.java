package org.example.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.SocketTimeoutException;
import java.net.http.HttpTimeoutException;

@Log4j2
@RestControllerAdvice
public class GlobalExceptionAdvice extends ResponseEntityExceptionHandler {
    @ExceptionHandler(NoPermissionException.class)
    ProblemDetail forbidden(NoPermissionException exception) {
        var message = exception.getMessage();
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
            message == null ? "Permission denied" : message);
    }

    @ExceptionHandler({IllegalArgumentException.class, ConstraintViolationException.class})
    ProblemDetail invalidInput(Exception exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Invalid request input");
    }

    @ExceptionHandler(RestClientException.class)
    ProblemDetail upstreamFailure(RestClientException exception) {
        log.error("External service request failed", exception);
        var status = HttpStatus.BAD_GATEWAY;
        if (exception instanceof ResourceAccessException) {
            for (Throwable cause = exception; cause != null; cause = cause.getCause()) {
                if (cause instanceof SocketTimeoutException || cause instanceof HttpTimeoutException) {
                    status = HttpStatus.GATEWAY_TIMEOUT;
                    break;
                }
            }
        }
        return ProblemDetail.forStatusAndDetail(status, "External service request failed");
    }

    @ExceptionHandler(Exception.class)
    ProblemDetail unexpectedFailure(Exception exception) {
        log.error("Unexpected request failure", exception);
        return ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
    }
}
