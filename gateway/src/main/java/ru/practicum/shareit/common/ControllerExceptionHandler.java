package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends HttpRequestResponseLogger {

    @ExceptionHandler
    public ProblemDetail handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request
    ) {
        final Map<String, String> errors = exception.getBindingResult().getFieldErrors().stream()
                .filter(e -> e.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (s1, s2) -> s1 + ", " + s2));
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Check that data you sent is correct");
        response.setProperty("errors", errors);
        log.warn("Model validation errors: {}", errors);
        logResponse(request, response);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleThrowable(
            final Throwable throwable,
            final HttpServletRequest request
    ) {
        log.error(throwable.getMessage(), throwable);
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Please contact site admin");
        logResponse(request, response);
        return response;
    }
}
