package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.UnsupportedBookingStateFilterException;
import ru.practicum.shareit.common.exception.ValidationException;

import java.util.Map;

@RestControllerAdvice
public class ControllerExceptionHandler extends BaseExceptionHandler {

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
    public ProblemDetail handleValidationException(
            final ValidationException exception,
            final HttpServletRequest request
    ) {
        final Map<String, String> errors = Map.of(exception.getProperty(), exception.getViolation());
        return handleValidationErrors(errors, request);
    }

    @ExceptionHandler
    public ProblemDetail handleUnsupportedBookingStateFilterException(
            final UnsupportedBookingStateFilterException exception,
            final HttpServletRequest request
    ) {
        log.warn("Unknown state to filter bookings: {}", exception.getInvalidValue());
        final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                "Check that data you sent is correct");
        response.setProperty("error", "Unknown state: %s".formatted(exception.getInvalidValue()));
        logResponse(request, response);
        return response;
    }

    @ExceptionHandler
    public ProblemDetail handleDataIntegrityViolationException(
            final DataIntegrityViolationException exception,
            final HttpServletRequest request
    ) {
        if (exception.getCause() instanceof org.hibernate.exception.ConstraintViolationException cause) {
            return switch (cause.getConstraintName()) {
                case "users_email_ux" -> {
                    log.warn(cause.getMessage());
                    final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT,
                            "Email already exists");
                    logResponse(request, response);
                    yield response;
                }
                case "requests_requester_id_fk" -> {
                    log.warn(cause.getMessage());
                    final ProblemDetail response = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN,
                            "You should be an authorized user to post a request");
                    logResponse(request, response);
                    yield response;
                }
                case null, default -> handleThrowable(exception, request);
            };
        }
        return handleThrowable(exception, request);
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
}
