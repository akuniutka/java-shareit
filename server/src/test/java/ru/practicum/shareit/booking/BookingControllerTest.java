package ru.practicum.shareit.booking;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;
import ru.practicum.shareit.common.exception.UnsupportedBookingStateFilterException;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingCreateDtoProxy;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingProxy;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingRetrieveDtoProxy;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class BookingControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(BookingController.class);

    private static final long USER_ID = 42L;
    private static final long BOOKING_ID = 1L;
    private static final String STATE = "ALL";
    private static final String WRONG_STATE = "WRONG VALUE";
    private static final BookingStatusFilter FILTER = BookingStatusFilter.ALL;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final boolean APPROVED = true;

    @Mock
    private BookingService mockService;

    @Mock
    private BookingMapper mockMapper;

    private InOrder inOrder;

    private BookingController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new BookingController(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        inOrder = inOrder(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void testCreateBooking() throws JSONException, IOException {
        when(mockMapper.mapToBooking(makeBookingCreateDtoProxy(), USER_ID)).thenReturn(makeBookingProxy());
        when(mockService.createBooking(makeBookingProxy())).thenReturn(makeBookingProxy());
        when(mockMapper.mapToDto(makeBookingProxy())).thenReturn(makeBookingRetrieveDtoProxy());

        final BookingRetrieveDto actual = controller.createBooking(USER_ID, makeBookingCreateDtoProxy(),
                mockHttpRequest);

        inOrder.verify(mockMapper).mapToBooking(makeBookingCreateDtoProxy(), USER_ID);
        inOrder.verify(mockService).createBooking(makeBookingProxy());
        inOrder.verify(mockMapper).mapToDto(makeBookingProxy());
        assertThat(actual, equalTo(makeBookingRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "create_booking.json", getClass());
    }

    @Test
    void testGetBooking() throws JSONException, IOException {
        when(mockService.getBooking(BOOKING_ID, USER_ID)).thenReturn(makeBookingProxy());
        when(mockMapper.mapToDto(makeBookingProxy())).thenReturn(makeBookingRetrieveDtoProxy());

        final BookingRetrieveDto actual = controller.getBooking(USER_ID, BOOKING_ID, mockHttpRequest);

        inOrder.verify(mockService).getBooking(BOOKING_ID, USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeBookingProxy());
        assertThat(actual, equalTo(makeBookingRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_booking.json", getClass());
    }

    @Test
    void testGetUserBookings() throws JSONException, IOException {
        when(mockService.getUserBookings(USER_ID, FILTER, FROM, SIZE)).thenReturn(List.of(makeBookingProxy()));
        when(mockMapper.mapToDto(List.of(makeBookingProxy()))).thenReturn(List.of(makeBookingRetrieveDtoProxy()));

        final List<BookingRetrieveDto> actual = controller.getUserBookings(USER_ID, STATE, FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getUserBookings(USER_ID, FILTER, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeBookingProxy()));
        assertThat(actual, contains(makeBookingRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_user_bookings.json", getClass());
    }

    @Test
    void testGetUserBookingsWhenUnsupportedFilter() throws JSONException, IOException {

        final UnsupportedBookingStateFilterException exception = assertThrows(
                UnsupportedBookingStateFilterException.class,
                () -> controller.getUserBookings(USER_ID, WRONG_STATE, FROM, SIZE, mockHttpRequest));

        assertThat(exception.getInvalidValue(), equalTo(WRONG_STATE));
        assertLogs(logListener.getEvents(), "get_user_bookings_wrong_filter.json", getClass());

        // Tune mock invocations count
        mockHttpRequest.getMethod();
        mockHttpRequest.getRequestURI();
        mockHttpRequest.getQueryString();
    }

    @Test
    void testGetOwnerBookings() throws JSONException, IOException {
        when(mockService.getOwnerBookings(USER_ID, FILTER, FROM, SIZE)).thenReturn(List.of(makeBookingProxy()));
        when(mockMapper.mapToDto(List.of(makeBookingProxy()))).thenReturn(List.of(makeBookingRetrieveDtoProxy()));

        final List<BookingRetrieveDto> actual = controller.getOwnerBookings(USER_ID, STATE, FROM, SIZE,
                mockHttpRequest);

        inOrder.verify(mockService).getOwnerBookings(USER_ID, FILTER, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeBookingProxy()));
        assertThat(actual, contains(makeBookingRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_owner_bookings.json", getClass());
    }

    @Test
    void testGetOwnerBookingsWhenUnsupportedFilter() throws JSONException, IOException {

        final UnsupportedBookingStateFilterException exception = assertThrows(
                UnsupportedBookingStateFilterException.class,
                () -> controller.getOwnerBookings(USER_ID, WRONG_STATE, FROM, SIZE, mockHttpRequest));

        assertThat(exception.getInvalidValue(), equalTo(WRONG_STATE));
        assertLogs(logListener.getEvents(), "get_owner_bookings_wrong_filter.json", getClass());

        // Tune mock invocations count
        mockHttpRequest.getMethod();
        mockHttpRequest.getRequestURI();
        mockHttpRequest.getQueryString();
    }

    @Test
    void testProcessBookingRequest() throws JSONException, IOException {
        when(mockService.applyBookingVerdict(BOOKING_ID, APPROVED, USER_ID)).thenReturn(makeBookingProxy());
        when(mockMapper.mapToDto(makeBookingProxy())).thenReturn(makeBookingRetrieveDtoProxy());

        final BookingRetrieveDto actual = controller.processBookingRequest(USER_ID, BOOKING_ID, APPROVED,
                mockHttpRequest);

        inOrder.verify(mockService).applyBookingVerdict(BOOKING_ID, APPROVED, USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeBookingProxy());
        assertThat(actual, equalTo(makeBookingRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "process_booking_request.json", getClass());
    }
}