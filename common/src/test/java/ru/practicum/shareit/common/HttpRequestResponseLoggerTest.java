package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.IOException;
import java.util.stream.Stream;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class HttpRequestResponseLoggerTest {

    private static final LogListener logListener =
            new LogListener(HttpRequestResponseLoggerTest.TestHttpRequestResponseLogger.class);

    private static final String METHOD = "POST";
    private static final String URI = "http://somehost/home";
    private static final String HEADER = "X-Sharer-User-Id";
    private static final String HEADER_VALUE = "42";
    private static final String QUERY_STRING = "value=none";
    private static final String BODY = "{\"id\":1}";

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    private HttpRequestResponseLogger logger;

    private static Stream<Arguments> getHeaderAndQueryString() {
        return Stream.of(
                Arguments.of(1, null, null),
                Arguments.of(2, HEADER_VALUE, null),
                Arguments.of(3, null, QUERY_STRING),
                Arguments.of(4, HEADER_VALUE, QUERY_STRING)
        );
    }

    private static Stream<Arguments> getQueryString() {
        return Stream.of(
                Arguments.of(1, null),
                Arguments.of(2, QUERY_STRING)
        );
    }

    private static Stream<Arguments> getQueryStringAndBody() {
        return Stream.of(
                Arguments.of(1, null, null),
                Arguments.of(2, QUERY_STRING, null),
                Arguments.of(3, null, BODY),
                Arguments.of(4, QUERY_STRING, BODY)
        );
    }

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        logger = new TestHttpRequestResponseLogger();
        when(mockHttpRequest.getMethod()).thenReturn(METHOD);
        when(mockHttpRequest.getRequestURI()).thenReturn(URI);
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        verify(mockHttpRequest).getMethod();
        verify(mockHttpRequest).getRequestURI();
        verify(mockHttpRequest).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenNoBody(final int caseNumber, final String headerValue, final String query) throws
            JSONException, IOException {
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);

        logger.logRequest(mockHttpRequest);

        verify(mockHttpRequest).getHeader(HEADER);
        assertLogs(logListener.getEvents(), "log_request_no_body_" + caseNumber + ".json", getClass());
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenBodyNull(final int caseNumber, final String headerValue, final String query) throws
            JSONException, IOException {
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);

        logger.logRequest(mockHttpRequest, null);

        verify(mockHttpRequest).getHeader(HEADER);
        assertLogs(logListener.getEvents(), "log_request_null_body_" + caseNumber + ".json", getClass());
    }

    @ParameterizedTest
    @MethodSource("getHeaderAndQueryString")
    void testLogRequestWhenBodyNonNull(final int caseNumber, final String headerValue, final String query) throws
            JSONException, IOException {
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(headerValue);
        when(mockHttpRequest.getQueryString()).thenReturn(query);

        logger.logRequest(mockHttpRequest, BODY);

        verify(mockHttpRequest).getHeader(HEADER);
        assertLogs(logListener.getEvents(), "log_request_with_body_" + caseNumber + ".json", getClass());
    }

    @ParameterizedTest
    @MethodSource("getQueryString")
    void testLogResponseWhenNoBody(final int caseNumber, final String query) throws JSONException, IOException {
        when(mockHttpRequest.getQueryString()).thenReturn(query);

        logger.logResponse(mockHttpRequest);

        assertLogs(logListener.getEvents(), "log_response_no_body_" + caseNumber + ".json", getClass());
    }

    @ParameterizedTest
    @MethodSource("getQueryStringAndBody")
    void testLogResponseWithBody(final int caseNumber, final String query, final String body) throws JSONException,
            IOException {
        when(mockHttpRequest.getQueryString()).thenReturn(query);

        logger.logResponse(mockHttpRequest, body);

        assertLogs(logListener.getEvents(), "log_response_with_body_" + caseNumber + ".json", getClass());
    }

    private static class TestHttpRequestResponseLogger extends HttpRequestResponseLogger {

    }
}