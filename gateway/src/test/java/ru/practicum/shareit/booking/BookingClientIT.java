package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBookingCreateDto;
import static ru.practicum.shareit.common.CommonUtils.loadJson;
import static ru.practicum.shareit.common.EqualToJson.equalToJson;
import static ru.practicum.shareit.common.HasContentType.hasContentType;
import static ru.practicum.shareit.common.HasJsonBody.hasJsonBody;
import static ru.practicum.shareit.common.HasStatus.hasStatus;

@RestClientTest(BookingClient.class)
class BookingClientIT {

    private static final String HEADER = "X-Sharer-User-Id";
    private static final long USER_ID = 42L;
    private static final long BOOKING_ID = 1L;
    private static final String BOOKING_STATE = "all";
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Value("${shareit-server.url}")
    private String serverUrl;

    private String baseUrl;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private BookingClient client;

    @BeforeEach
    void setUp() {
        baseUrl = serverUrl + "/bookings";
        server.reset();
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void testCreateBooking() throws IOException {
        final BookingCreateDto dto = makeTestBookingCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_booking.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
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
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createBooking(USER_ID, dto));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.NOT_FOUND),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testCreateBookingWhenInternalServerError() throws IOException {
        final BookingCreateDto dto = makeTestBookingCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_booking_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createBooking(USER_ID, dto));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testGetBooking() throws IOException {
        final String body = loadJson("get_booking.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getBooking(USER_ID, BOOKING_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetBookingWhenNotFound() throws IOException {
        final String body = loadJson("get_booking_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getBooking(USER_ID, BOOKING_ID));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.NOT_FOUND),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testGetBookingWhenInternalServerError() throws IOException {
        final String body = loadJson("get_booking_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getBooking(USER_ID, BOOKING_ID));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testGetUserBookings() throws IOException {
        final String body = loadJson("get_user_bookings.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUserBookingsWhenEmpty() throws IOException {
        final String body = loadJson("get_user_bookings_empty.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUserBookingsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_user_bookings_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUserBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testGetOwnerBookings() throws IOException {
        final String body = loadJson("get_owner_bookings.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnerBookingsWhenEmpty() throws IOException {
        final String body = loadJson("get_owner_bookings_empty.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnerBookingsWhenNoItems() throws IOException {
        final String body = loadJson("get_owner_bookings_no_items.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.FORBIDDEN),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testGetOwnerBookingsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_owner_bookings_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl
                        + "/owner?state=%s&from=%d&size=%d".formatted(BOOKING_STATE, FROM, SIZE)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnerBookings(USER_ID, BOOKING_STATE, FROM, SIZE));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testProcessBookingRequest() throws IOException {
        final String body = loadJson("process_booking.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.processBookingRequest(USER_ID, BOOKING_ID, true);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testProcessBookingRequestWhenNotOwner() throws IOException {
        final String body = loadJson("process_booking_not_owner.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.FORBIDDEN)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.processBookingRequest(USER_ID, BOOKING_ID, true));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.FORBIDDEN),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }

    @Test
    void testProcessBookingRequestWhenInternalServerError() throws IOException {
        final String body = loadJson("process_booking_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + BOOKING_ID + "?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(USER_ID)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.processBookingRequest(USER_ID, BOOKING_ID, true));

        assertThat(exception, allOf(
                hasStatus(HttpStatus.INTERNAL_SERVER_ERROR),
                hasContentType(MediaType.APPLICATION_PROBLEM_JSON),
                hasJsonBody(body)));
    }
}
