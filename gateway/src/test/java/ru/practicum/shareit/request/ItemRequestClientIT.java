package ru.practicum.shareit.request;

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
import static ru.practicum.shareit.common.CommonUtils.loadJson;
import static ru.practicum.shareit.common.EqualToJson.equalToJson;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequestCreateDto;

@RestClientTest(ItemRequestClient.class)
class ItemRequestClientIT extends AbstractClientIT {

    private static final long USER_ID = 42L;
    private static final long ITEM_REQUEST_ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemRequestClient client;

    @BeforeEach
    void setUp() {
        basePath = "/requests";
        mockServer.reset();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void testCreateItemRequest() throws IOException {
        final ItemRequestCreateDto dto = makeTestItemRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_request.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createItemRequest(USER_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateItemRequestWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createItemRequest(USER_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot create item request: is null"));
    }

    @Test
    void testCreateItemRequestWhenUserNotFound() throws IOException {
        final ItemRequestCreateDto dto = makeTestItemRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_request_user_not_found.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItemRequest(USER_ID, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testCreateItemRequestWhenInternalServerError() throws IOException {
        final ItemRequestCreateDto dto = makeTestItemRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_request_internal_server_error.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItemRequest(USER_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItemRequest() throws IOException {
        final String body = loadJson("get_item_request.json", getClass());
        expectGet("/" + ITEM_REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItemRequest(USER_ID, ITEM_REQUEST_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemRequestWhenNotFound() throws IOException {
        final String body = loadJson("get_item_request_not_found.json", getClass());
        expectGet("/" + ITEM_REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItemRequest(USER_ID, ITEM_REQUEST_ID));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetItemRequestWhenInternalServerError() throws IOException {
        final String body = loadJson("get_item_request_internal_server_error.json", getClass());
        expectGet("/" + ITEM_REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItemRequest(USER_ID, ITEM_REQUEST_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetOwnItemRequests() throws IOException {
        final String body = loadJson("get_own_item_requests.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnItemRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnItemRequestsWhenEmpty() throws IOException {
        final String body = loadJson("get_own_item_requests_empty.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnItemRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnItemRequestsWhenUserNotFound() throws IOException {
        final String body = loadJson("get_own_item_requests_user_not_found.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnItemRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetOwnItemRequestsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_own_item_requests_internal_server_error.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnItemRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetOthersItemRequests() throws IOException {
        final String body = loadJson("get_others_item_requests.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOthersItemRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOthersItemRequestsWhenEmpty() throws IOException {
        final String body = loadJson("get_others_item_requests_empty.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOthersItemRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOthersItemRequestsWhenUserNotFound() throws IOException {
        final String body = loadJson("get_others_item_requests_user_not_found.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOthersItemRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetOthersItemRequestsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_others_item_requests_internal_server_error.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOthersItemRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }
}