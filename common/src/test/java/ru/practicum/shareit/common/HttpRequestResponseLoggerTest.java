package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import java.util.stream.Stream;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class HttpRequestResponseLoggerTest {

    private static final String METHOD = "method";
    private static final String URI = "http://somehost/home";
    private static final String HEADER = "X-Sharer-User-Id";
    private static final String HEADER_VALUE = "42";
    private static final String QUERY_STRING = "value=none";
    private static final String BODY = "{\"id\":1}";

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Mock
    private Logger mockLog;

    private HttpRequestResponseLogger logger;

    private static Stream<Arguments> getHeaderAndQueryString() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(HEADER_VALUE, null),
                Arguments.of(null, QUERY_STRING),
                Arguments.of(HEADER_VALUE, QUERY_STRING)
        );
    }

    private static Stream<String> getQueryString() {
        return Stream.of(null, QUERY_STRING);
    }

    private static Stream<Arguments> getQueryStringAndBody() {
        return Stream.of(
                Arguments.of(null, null),
                Arguments.of(QUERY_STRING, null),
                Arguments.of(null, BODY),
                Arguments.of(QUERY_STRING, BODY)
        );
    }

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        logger = Mockito.mock(HttpRequestResponseLogger.class, Mockito.CALLS_REAL_METHODS);
        logger.setLogger(mockLog);
        when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        when(mockHttpRequest.getRequestURI()).thenReturn(URI);
    }

    @AfterEach
    void tearDown() throws Exception {
        verify(mockHttpRequest).getMethod();
        verify(mockHttpRequest).getRequestURI();
        verify(mockHttpRequest).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest, mockLog);
        openMocks.close();
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenNoBody(final String headerValue, final String query) {
        final String headerStr = headerValue == null ? "" : " (%s: %s)".formatted(HEADER, headerValue);
        final String queryStr = query == null ? "" : "?" + query;
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);
        doNothing().when(mockLog).info("Received {} at {}{}{}", METHOD, URI, queryStr, headerStr);

        logger.logRequest(mockHttpRequest);

        verify(mockHttpRequest).getHeader(HEADER);
        verify(mockLog).info("Received {} at {}{}{}", METHOD, URI, queryStr, headerStr);
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenBodyNull(final String headerValue, final String query) {
        final String headerStr = headerValue == null ? "" : " (%s: %s)".formatted(HEADER, headerValue);
        final String queryStr = query == null ? "" : "?" + query;
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);
        doNothing().when(mockLog).info("Received {} at {}{}: {}{}", METHOD, URI, queryStr, null, headerStr);

        logger.logRequest(mockHttpRequest, null);

        verify(mockHttpRequest).getHeader(HEADER);
        verify(mockLog).info("Received {} at {}{}: {}{}", METHOD, URI, queryStr, null, headerStr);
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenBodyNonNull(final String headerValue, final String query) {
        final String headerStr = headerValue == null ? "" : " (%s: %s)".formatted(HEADER, headerValue);
        final String queryStr = query == null ? "" : "?" + query;
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);
        doNothing().when(mockLog).info("Received {} at {}{}: {}{}", METHOD, URI, queryStr, BODY, headerStr);

        logger.logRequest(mockHttpRequest, BODY);

        verify(mockHttpRequest).getHeader(HEADER);
        verify(mockLog).info("Received {} at {}{}: {}{}", METHOD, URI, queryStr, BODY, headerStr);
    }

    @ParameterizedTest
    @MethodSource("getQueryString")
    void testLogResponseWhenNoQueryBody(final String query) {
        final String queryStr = query == null ? "" : "?" + query;
        when(mockHttpRequest.getQueryString()).thenReturn(query);
        doNothing().when(mockLog).info("Responded to {} {}{} with no body", METHOD, URI, queryStr);

        logger.logResponse(mockHttpRequest);

        verify(mockLog).info("Responded to {} {}{} with no body", METHOD, URI, queryStr);
    }

    @ParameterizedTest
    @MethodSource("getQueryStringAndBody")
    void testLogResponseWithBody(final String query, final String body) {
        final String queryStr = query == null ? "" : "?" + query;
        when(mockHttpRequest.getQueryString()).thenReturn(query);
        doNothing().when(mockLog).info("Responded to {} {}{}: {}", METHOD, URI, queryStr, body);

        logger.logResponse(mockHttpRequest, body);

        verify(mockLog).info("Responded to {} {}{}: {}", METHOD, URI, queryStr, body);
    }
}