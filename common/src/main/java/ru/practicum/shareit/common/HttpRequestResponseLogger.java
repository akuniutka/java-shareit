package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class HttpRequestResponseLogger {

    protected Logger log = LoggerFactory.getLogger(getClass());

    protected void logRequest(final HttpServletRequest request) {
        final String queryString = translateQueryString(request);
        final String userHeader = translateUserHeader(request);
        log.info("Received {} at {}{}{}", request.getMethod(), request.getRequestURI(), queryString, userHeader);
    }

    protected void logRequest(final HttpServletRequest request, final Object body) {
        final String queryString = translateQueryString(request);
        final String userHeader = translateUserHeader(request);
        log.info("Received {} at {}{}: {}{}", request.getMethod(), request.getRequestURI(), queryString, body,
                userHeader);
    }

    protected void logResponse(final HttpServletRequest request) {
        final String queryString = translateQueryString(request);
        log.info("Responded to {} {}{} with no body", request.getMethod(), request.getRequestURI(), queryString);
    }

    protected void logResponse(final HttpServletRequest request, final Object body) {
        final String queryString = translateQueryString(request);
        log.info("Responded to {} {}{}: {}", request.getMethod(), request.getRequestURI(), queryString, body);
    }

    protected String translateQueryString(final HttpServletRequest request) {
        return Optional.ofNullable(request.getQueryString()).map(s -> "?" + s).orElse("");
    }

    protected String translateUserHeader(final HttpServletRequest request) {
        return Optional.ofNullable(request.getHeader("X-Sharer-User-Id"))
                .map(" (X-Sharer-User-Id: %s)"::formatted)
                .orElse("");
    }
}
