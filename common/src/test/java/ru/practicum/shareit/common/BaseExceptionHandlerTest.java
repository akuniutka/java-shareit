package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.http.ProblemDetail;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class BaseExceptionHandlerTest {

    private static final LogListener logListener = new LogListener(TestExceptionHandler.class);

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    private BaseExceptionHandler handler;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        handler = new TestExceptionHandler();
        when(mockHttpRequest.getMethod()).thenReturn("POST");
        when(mockHttpRequest.getRequestURI()).thenReturn("http://somehost/home");
        when(mockHttpRequest.getQueryString()).thenReturn("value=none");
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

    @Test
    void testHandleMethodArgumentNotValidException() throws JSONException, IOException {
        final MethodArgumentNotValidException mockException = Mockito.mock(MethodArgumentNotValidException.class);
        final FieldError nameLengthError = new FieldError("user", "name", "cannot exceed characters");
        final FieldError charactersError = new FieldError("user", "name", "cannot contain whitespaces");
        final FieldError malformedError = Mockito.mock(FieldError.class);
        final Map<String, String> errors = Map.of("name", "cannot exceed characters, cannot contain whitespaces");
        when(mockException.getFieldErrors()).thenReturn(List.of(nameLengthError, charactersError, malformedError));
        when(malformedError.getDefaultMessage()).thenReturn(null);

        final ProblemDetail response = handler.handleMethodArgumentNotValidException(mockException, mockHttpRequest);

        verify(mockException).getFieldErrors();
        verify(malformedError).getDefaultMessage();
        assertEquals(400, response.getStatus());
        assertEquals("Check that data you sent is correct", response.getDetail());
        assertNotNull(response.getProperties());
        assertEquals(errors, response.getProperties().get("error"));
        assertLogs(logListener.getEvents(), "method_argument_not_valid_exception.json", getClass());
    }

    @Test
    void testHandleThrowable() throws JSONException, IOException {
        final Throwable throwable = new Throwable("Test message", new Throwable());

        final ProblemDetail response = handler.handleThrowable(throwable, mockHttpRequest);

        assertEquals(500, response.getStatus());
        assertEquals("Please contact site admin", response.getDetail());
        assertLogs(logListener.getEvents(), "throwable.json", getClass());
    }

    private static class TestExceptionHandler extends BaseExceptionHandler {

    }
}