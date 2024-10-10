package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Optional;

@RestControllerAdvice
public class ControllerExceptionHandler extends BaseExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<Object> handleHttpStatusCodeException(
            final HttpStatusCodeException exception,
            final HttpServletRequest request
    ) {
        log.warn("Server responded with error status code {}", exception.getMessage());
        final ResponseEntity<Object> response = ResponseEntity
                .status(exception.getStatusCode())
                .contentType(Optional.ofNullable(exception.getResponseHeaders())
                        .map(HttpHeaders::getContentType)
                        .orElse(MediaType.APPLICATION_JSON))
                .body(exception.getResponseBodyAs(Object.class));
        logResponse(request, response);
        return response;
    }
}
