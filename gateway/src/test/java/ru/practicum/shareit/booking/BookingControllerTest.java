package ru.practicum.shareit.booking;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingUtils.deepEqualTo;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBookingCreateDto;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class BookingControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(BookingController.class);

    private static final long USER_ID = 42L;
    private static final long BOOKING_ID = 1L;
    private static final String STATE = "all";
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final boolean APPROVED = true;

    @Mock
    private BookingClient client;

    @Captor
    private ArgumentCaptor<BookingCreateDto> bookingCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private BookingController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new BookingController(client);
        logListener.startListen();
        logListener.reset();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(client);
        super.tearDown();
    }

    @Test
    void testCreateBooking() throws JSONException, IOException {
        when(client.createBooking(anyLong(), any(BookingCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createBooking(USER_ID, makeTestBookingCreateDto(), mockHttpRequest);

        verify(client).createBooking(userIdCaptor.capture(), bookingCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(bookingCreateDtoCaptor.getValue(), deepEqualTo(makeTestBookingCreateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "create_booking.json", getClass());
    }

    @Test
    void testGetBooking() throws JSONException, IOException {
        when(client.getBooking(USER_ID, BOOKING_ID)).thenReturn(testResponse);

        final Object actual = controller.getBooking(USER_ID, BOOKING_ID, mockHttpRequest);

        verify(client).getBooking(USER_ID, BOOKING_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_booking.json", getClass());
    }

    @Test
    void testUserBookings() throws JSONException, IOException {
        when(client.getUserBookings(USER_ID, STATE, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getUserBookings(USER_ID, STATE, FROM, SIZE, mockHttpRequest);

        verify(client).getUserBookings(USER_ID, STATE, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_user_bookings.json", getClass());
    }

    @Test
    void testOwnerBookings() throws JSONException, IOException {
        when(client.getOwnerBookings(USER_ID, STATE, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getOwnerBookings(USER_ID, STATE, FROM, SIZE, mockHttpRequest);

        verify(client).getOwnerBookings(USER_ID, STATE, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_owner_bookings.json", getClass());
    }

    @Test
    void testProcessBookingRequest() throws JSONException, IOException {
        when(client.processBookingRequest(USER_ID, BOOKING_ID, APPROVED)).thenReturn(testResponse);

        final Object actual = controller.processBookingRequest(USER_ID, BOOKING_ID, APPROVED, mockHttpRequest);

        verify(client).processBookingRequest(USER_ID, BOOKING_ID, APPROVED);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "process_booking_request.json", getClass());
    }
}
