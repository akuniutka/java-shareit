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
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.loadJson;
import static ru.practicum.shareit.common.EqualToJson.equalToJson;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestCreateDto;

@RestClientTest(RequestClient.class)
class RequestClientIT extends AbstractClientIT {

    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private RequestClient client;

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
    void testCreateRequest() throws IOException {
        final RequestCreateDto dto = makeTestRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_request.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createRequest(USER_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateRequestWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createRequest(USER_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot create item request: is null"));
    }

    @Test
    void testCreateRequestWhen4XxError() throws IOException {
        final RequestCreateDto dto = makeTestRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_request_user_not_found.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createRequest(USER_ID, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testCreateRequestWhen5XxError() throws IOException {
        final RequestCreateDto dto = makeTestRequestCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_request_internal_server_error.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createRequest(USER_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetRequest() throws IOException {
        final String body = loadJson("get_request.json", getClass());
        expectGet("/" + REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getRequest(USER_ID, REQUEST_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetRequestWhen4XxError() throws IOException {
        final String body = loadJson("get_request_not_found.json", getClass());
        expectGet("/" + REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getRequest(USER_ID, REQUEST_ID));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetRequestWhen5XxError() throws IOException {
        final String body = loadJson("get_request_internal_server_error.json", getClass());
        expectGet("/" + REQUEST_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getRequest(USER_ID, REQUEST_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetOwnRequests() throws IOException {
        final String body = loadJson("get_own_requests.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnRequestsWhenEmpty() throws IOException {
        final String body = loadJson("get_own_requests_empty.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOwnRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOwnRequestsWhen4XxError() throws IOException {
        final String body = loadJson("get_own_requests_user_not_found.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetOwnRequestsWhen5XxError() throws IOException {
        final String body = loadJson("get_own_requests_internal_server_error.json", getClass());
        expectGet("?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOwnRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetOthersRequests() throws IOException {
        final String body = loadJson("get_others_requests.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOthersRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOthersRequestsWhenEmpty() throws IOException {
        final String body = loadJson("get_others_requests_empty.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getOthersRequests(USER_ID, FROM, SIZE);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetOthersRequestsWhen4XxError() throws IOException {
        final String body = loadJson("get_others_requests_user_not_found.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOthersRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetOthersRequestsWhen5XxError() throws IOException {
        final String body = loadJson("get_others_requests_internal_server_error.json", getClass());
        expectGet("/all?from=%s&size=%s".formatted(FROM, SIZE), USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getOthersRequests(USER_ID, FROM, SIZE));

        assertThat(exception, isInternalServerError(body));
    }
}