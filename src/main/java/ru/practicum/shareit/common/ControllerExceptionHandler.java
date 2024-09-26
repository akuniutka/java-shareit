package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.DuplicateDataException;
import ru.practicum.shareit.common.exception.NotFoundException;

import java.util.List;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler extends BaseController {

    @ExceptionHandler
    public ProblemDetail handleNotFoundException(
            final NotFoundException exception,
            final HttpServletRequest request
    ) {
        log.warn("Model '{}' with id = {} not found", exception.getModelName(), exception.getModelId());
        final String detail = "Check that id of %s is correct (you sent %s)".formatted(exception.getModelName(),
                exception.getModelId());
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, detail);
        logResponse(request, response);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleConstraintViolationException(
            final ConstraintViolationException exception,
            final HttpServletRequest request
    ) {
        final List<Map<String, String>> errors = exception.getConstraintViolations().stream()
                .map(v -> Map.of(v.getPropertyPath().toString(), v.getMessage()))
                .toList();
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Check that data you sent is correct");
        response.setProperty("errors", errors);
        log.warn("Model validation errors: {}", errors);
        logResponse(request, response);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleDuplicateDataException(
            final DuplicateDataException exception,
            final HttpServletRequest request
    ) {
        log.warn(exception.getMessage());
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, exception.getMessage());
        logResponse(request, response);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleActionNotAllowedException(
            final ActionNotAllowedException exception,
            final HttpServletRequest request
    ) {
        log.warn(exception.getMessage());
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
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
