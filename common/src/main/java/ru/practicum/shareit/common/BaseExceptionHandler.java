package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;
import java.util.stream.Collectors;

public abstract class BaseExceptionHandler extends HttpRequestResponseLogger {

    @ExceptionHandler
    protected ProblemDetail handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception,
            final HttpServletRequest request
    ) {
        final Map<String, String> errors = exception.getFieldErrors().stream()
                .filter(e -> e.getDefaultMessage() != null)
                .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage,
                        (s1, s2) -> s1 + ", " + s2));
        return handleValidationErrors(errors, request);
    }

    @ExceptionHandler
    protected ProblemDetail handleThrowable(
            final Throwable throwable,
            final HttpServletRequest request
    ) {
        log.error(throwable.getMessage(), throwable);
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                "Please contact site admin");
        logResponse(request, response);
        return response;
    }

    protected ProblemDetail handleValidationErrors(
            final Map<String, String> errors,
            final HttpServletRequest request
    ) {
        log.warn("Model validation errors: {}", errors);
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Check that data you sent is correct");
        // TODO: after sprint #16 replace "error" with "errors"
        response.setProperty("error", errors);
        logResponse(request, response);
        return response;
    }
}
