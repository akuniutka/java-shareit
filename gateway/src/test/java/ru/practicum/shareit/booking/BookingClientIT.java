package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;
import ru.practicum.shareit.common.AbstractClientIT;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBookingCreateDto;
import static ru.practicum.shareit.common.CommonUtils.loadJson;
import static ru.practicum.shareit.common.EqualToJson.equalToJson;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isForbidden;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;

@RestClientTest(BookingClient.class)
class BookingClientIT extends AbstractClientIT {

    private static final long USER_ID = 42L;
    private static final long BOOKING_ID = 1L;
    private static final String BOOKING_STATE = "all";
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookingClient client;

    @BeforeEach
    void setUp() {
        basePath = "/bookings";
        mockServer.reset();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void testCreateBooking() throws IOException {
        final BookingCreateDto dto = makeTestBookingCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_booking.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createBooking(USER_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateBookingWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createBooking(USER_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot create booking: is null"));
    }

    @Test
    void testCreateBookingWhenUserNotFound() throws IOException {
        final BookingCreateDto dto = makeTestBookingCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_booking_user_not_found.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createBooking(USER_ID, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testCreateBookingWhenInternalServerError() throws IOException {
        final BookingCreateDto dto = makeTestBookingCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_booking_internal_server_error.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createBooking(USER_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetBooking() throws IOException {
        final String body = loadJson("get_booking.json", getClass());
        expectGet("/" + BOOKING_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getBooking(USER_ID, BOOKING_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetBookingWhenNotFound() throws IOException {
        final String body = loadJson("get_booking_not_found.json", getClass());
        expectGet("/" + BOOKING_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getBooking(USER_ID, BOOKING_ID));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetBookingWhenInternalServerError() throws IOException {
        final String body = loadJson("get_booking_internal_server_error.json", getClass());
        expectGet("/" + BOOKING_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getBooking(USER_ID, BOOKING_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetUserBookings() throws IOException {
        final String body = loadJson("get_user_bookings.json", getClass());
        expectGet("?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUserBookingsWhenEmpty() throws IOException {
        final String body = loadJson("get_user_bookings_empty.json", getClass());
        expectGet("?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUserBookingsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_user_bookings_internal_server_error.json", getClass());
        expectGet("?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetOwnerBookings() throws IOException {
        final String body = loadJson("get_owner_bookings.json", getClass());
        expectGet("/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnerBookingsWhenEmpty() throws IOException {
        final String body = loadJson("get_owner_bookings_empty.json", getClass());
        expectGet("/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnerBookingsWhenNoItems() throws IOException {
        final String body = loadJson("get_owner_bookings_no_items.json", getClass());
        expectGet("/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, isForbidden(body));
    }

    @Test
    void testGetOwnerBookingsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_owner_bookings_internal_server_error.json", getClass());
        expectGet("/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testProcessBookingRequest() throws IOException {
        final String body = loadJson("process_booking.json", getClass());
        expectPatch("/" + BOOKING_ID + "?approved=true", USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.processBookingRequest(USER_ID, BOOKING_ID, true);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testProcessBookingRequestWhenNotOwner() throws IOException {
        final String body = loadJson("process_booking_not_owner.json", getClass());
        expectPatch("/" + BOOKING_ID + "?approved=true", USER_ID)
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.processBookingRequest(USER_ID, BOOKING_ID, true));

        assertThat(exception, isForbidden(body));
    }

    @Test
    void testProcessBookingRequestWhenInternalServerError() throws IOException {
        final String body = loadJson("process_booking_internal_server_error.json", getClass());
        expectPatch("/" + BOOKING_ID + "?approved=true", USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.processBookingRequest(USER_ID, BOOKING_ID, true));

        assertThat(exception, isInternalServerError(body));
    }
}
