package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
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

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.shareit.common.CommonUtils.getBody;
import static ru.practicum.shareit.common.CommonUtils.getContentType;
import static ru.practicum.shareit.common.CommonUtils.loadJson;

@RestClientTest(UserClient.class)
class UserClientIT {

    @Value("${shareit-server.url}")
    private String serverUrl;

    private String baseUrl;

    @Autowired
    private UserClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
        baseUrl = serverUrl + "/users";
        server.reset();
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void testCreateUser() throws IOException, JSONException {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createUser(dto);

        final String actual = mapper.writeValueAsString(response);
        JSONAssert.assertEquals(body, actual, false);
    }

    @Test
    void testCreateUserWhenDuplicateEmail() throws IOException, JSONException {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user_duplicate_email.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createUser(dto));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testCreateUserWhenInternalServerError() throws IOException, JSONException {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createUser(dto));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testGetUser() throws IOException, JSONException {
        final String body = loadJson("get_user.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUser(1L);

        final String actual = mapper.writeValueAsString(response);
        JSONAssert.assertEquals(body, actual, false);
    }

    @Test
    void testGetUserWhenNotFound() throws IOException, JSONException {
        final String body = loadJson("get_user_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUser(1L));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testGetUserWhenInternalServerError() throws IOException, JSONException {
        final String body = loadJson("get_user_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUser(1L));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testGetUsers() throws IOException, JSONException {
        final String body = loadJson("get_users.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUsers();

        final String actual = mapper.writeValueAsString(response);
        JSONAssert.assertEquals(body, actual, false);
    }

    @Test
    void testGetUsersWhenEmpty() throws IOException, JSONException {
        final String body = loadJson("get_users_empty.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUsers();

        final String actual = mapper.writeValueAsString(response);
        JSONAssert.assertEquals(body, actual, false);
    }

    @Test
    void testGetUsersWhenInternalServerError() throws IOException, JSONException {
        final String body = loadJson("get_users_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUsers());

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testUpdateUser() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.updateUser(1L, dto);

        final String actual = mapper.writeValueAsString(response);
        JSONAssert.assertEquals(body, actual, false);
    }

    @Test
    void testUpdateUserWhenDuplicateEmail() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user_duplicate_email.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateUser(1L, dto));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testUpdateUserWhenNotFound() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateUser(1L, dto));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testUpdateUserWhenInternalServerError() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateUser(1L, dto));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }

    @Test
    void testDeleteUser() {
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK));

        client.deleteUser(1L);
    }

    @Test
    void testDeleteUserWhenInternalServerError() throws IOException, JSONException {
        final String body = loadJson("delete_user_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.deleteUser(1L));

        assertThat(exception.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(getContentType(exception), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        JSONAssert.assertEquals(body, getBody(exception), false);
    }
}