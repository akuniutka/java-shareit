package ru.practicum.shareit.common;

import jakarta.servlet.http.HttpServletRequest;
import org.hibernate.exception.ConstraintViolationException;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ProblemDetail;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.UnsupportedBookingStateFilterException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasEntry;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class ControllerExceptionHandlerTest {

    private static final LogListener logListener = new LogListener(ControllerExceptionHandler.class);

    private AutoCloseable openMocks;

    @Mock
    private HttpServletRequest mockHttpRequest;

    private ControllerExceptionHandler handler;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        handler = new ControllerExceptionHandler();
        Mockito.when(mockHttpRequest.getMethod()).thenReturn("POST");
        Mockito.when(mockHttpRequest.getRequestURI()).thenReturn("http://somehost/home");
        Mockito.when(mockHttpRequest.getQueryString()).thenReturn("value=none");
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verify(mockHttpRequest).getMethod();
        Mockito.verify(mockHttpRequest).getRequestURI();
        Mockito.verify(mockHttpRequest).getQueryString();
        Mockito.verifyNoMoreInteractions(mockHttpRequest);
        openMocks.close();
    }

    @Test
    void testHandleNotFoundException() throws JSONException, IOException {
        final NotFoundException exception = new NotFoundException(User.class, 42L);

        final ProblemDetail response = handler.handleNotFoundException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(404));
        assertThat(response.getDetail(), equalTo("Check that id of user is correct (you sent 42)"));
        assertLogs(logListener.getEvents(), "not_found_exception.json", getClass());
    }

    @Test
    void testHandleValidationException() throws JSONException, IOException {
        final ValidationException exception = new ValidationException("end", "should be after start");

        final ProblemDetail response = handler.handleValidationException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(400));
        assertThat(response.getDetail(), equalTo("Check that data you sent is correct"));
        assertThat(response.getProperties(), hasEntry("error", Map.of("end", "should be after start")));
        assertLogs(logListener.getEvents(), "validation_exception.json", getClass());
    }

    @Test
    void testHandleUnsupportedBookingStateFilterException() throws JSONException, IOException {
        final UnsupportedBookingStateFilterException exception = new UnsupportedBookingStateFilterException("wrong");

        final ProblemDetail response = handler.handleUnsupportedBookingStateFilterException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(400));
        assertThat(response.getDetail(), equalTo("Check that data you sent is correct"));
        assertThat(response.getProperties(), hasEntry("error", "Unknown state: wrong"));
        assertLogs(logListener.getEvents(), "unsupported_booking_state_filter_exception.json", getClass());
    }

    @Test
    void testHandleDataIntegrityViolationExceptionWhenDuplicateEmail() throws JSONException, IOException {
        final ConstraintViolationException mockCause = Mockito.mock(ConstraintViolationException.class);
        Mockito.when(mockCause.getMessage()).thenReturn("root message");
        Mockito.when(mockCause.getConstraintName()).thenReturn("users_email_ux");
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("message", mockCause);

        final ProblemDetail response = handler.handleDataIntegrityViolationException(exception, mockHttpRequest);

        Mockito.verify(mockCause).getMessage();
        Mockito.verify(mockCause).getConstraintName();
        Mockito.verifyNoMoreInteractions(mockCause);
        assertThat(response.getStatus(), equalTo(409));
        assertThat(response.getDetail(), equalTo("Email already exists"));
        assertLogs(logListener.getEvents(), "data_integrity_violation_exception_email.json", getClass());
    }

    @Test
    void testHandleDataIntegrityViolationExceptionWhenRequesterNotExist() throws JSONException, IOException {
        final ConstraintViolationException mockCause = Mockito.mock(ConstraintViolationException.class);
        Mockito.when(mockCause.getMessage()).thenReturn("root message");
        Mockito.when(mockCause.getConstraintName()).thenReturn("requests_requester_id_fk");
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("message", mockCause);

        final ProblemDetail response = handler.handleDataIntegrityViolationException(exception, mockHttpRequest);

        Mockito.verify(mockCause).getMessage();
        Mockito.verify(mockCause).getConstraintName();
        Mockito.verifyNoMoreInteractions(mockCause);
        assertThat(response.getStatus(), equalTo(403));
        assertThat(response.getDetail(), equalTo("You should be an authorized user to post a request"));
        assertLogs(logListener.getEvents(), "data_integrity_violation_exception_user.json", getClass());
    }

    @Test
    void testHandleDataIntegrityViolationExceptionWhenUnknownConstraint() throws JSONException, IOException {
        final ConstraintViolationException mockCause = Mockito.mock(ConstraintViolationException.class);
        Mockito.when(mockCause.getConstraintName()).thenReturn("unknown_constraint");
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("message", mockCause);

        final ProblemDetail response = handler.handleDataIntegrityViolationException(exception, mockHttpRequest);

        Mockito.verify(mockCause).getConstraintName();
        assertThat(response.getStatus(), equalTo(500));
        assertThat(response.getDetail(), equalTo("Please contact site admin"));
        assertLogs(logListener.getEvents(), "data_integrity_violation_exception_default.json", getClass());
    }

    @Test
    void testHandleDataIntegrityViolationExceptionWhenNullConstraint() throws JSONException, IOException {
        final ConstraintViolationException mockCause = Mockito.mock(ConstraintViolationException.class);
        Mockito.when(mockCause.getConstraintName()).thenReturn(null);
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("message", mockCause);

        final ProblemDetail response = handler.handleDataIntegrityViolationException(exception, mockHttpRequest);

        Mockito.verify(mockCause).getConstraintName();
        assertThat(response.getStatus(), equalTo(500));
        assertThat(response.getDetail(), equalTo("Please contact site admin"));
        assertLogs(logListener.getEvents(), "data_integrity_violation_exception_null_constraint.json", getClass());
    }

    @Test
    void testHandleDataIntegrityViolationExceptionWhenNotConstraint() throws JSONException, IOException {
        final DataIntegrityViolationException exception = new DataIntegrityViolationException("message",
                new Throwable());

        final ProblemDetail response = handler.handleDataIntegrityViolationException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(500));
        assertThat(response.getDetail(), equalTo("Please contact site admin"));
        assertLogs(logListener.getEvents(), "data_integrity_violation_exception_not_constraint.json", getClass());
    }

    @Test
    void testHandleActionNotAllowedException() throws JSONException, IOException {
        final ActionNotAllowedException exception = new ActionNotAllowedException("not enough power");

        final ProblemDetail response = handler.handleActionNotAllowedException(exception, mockHttpRequest);

        assertThat(response.getStatus(), equalTo(403));
        assertThat(response.getDetail(), equalTo("not enough power"));
        assertLogs(logListener.getEvents(), "action_not_allowed_exception.json", getClass());
    }
}