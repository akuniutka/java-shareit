package ru.practicum.shareit.user;

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
import static ru.practicum.shareit.common.ErrorResponseMatchers.isConflict;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;
import static ru.practicum.shareit.user.UserUtils.makeTestUserCreateDto;
import static ru.practicum.shareit.user.UserUtils.makeTestUserUpdateDto;

@RestClientTest(UserClient.class)
class UserClientIT extends AbstractClientIT {

    private static final long USER_ID = 1L;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private UserClient client;

    @BeforeEach
    void setUp() {
        basePath = "/users";
        mockServer.reset();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void testCreateUser() throws IOException {
        final UserCreateDto dto = makeTestUserCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user.json", getClass());
        expectPost(dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createUser(dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateUserWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createUser(null));

        assertThat(exception.getMessage(), equalTo("Cannot create user: is null"));
    }

    @Test
    void testCreateUserWhenDuplicateEmail() throws IOException {
        final UserCreateDto dto = makeTestUserCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user_duplicate_email.json", getClass());
        expectPost(dtoJson)
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createUser(dto));

        assertThat(exception, isConflict(body));
    }

    @Test
    void testCreateUserWhenInternalServerError() throws IOException {
        final UserCreateDto dto = makeTestUserCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_user_internal_server_error.json", getClass());
        expectPost(dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createUser(dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetUser() throws IOException {
        final String body = loadJson("get_user.json", getClass());
        expectGet("/" + USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUser(USER_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUserWhenNotFound() throws IOException {
        final String body = loadJson("get_user_not_found.json", getClass());
        expectGet("/" + USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUser(USER_ID));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetUserWhenInternalServerError() throws IOException {
        final String body = loadJson("get_user_internal_server_error.json", getClass());
        expectGet("/" + USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUser(USER_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetUsers() throws IOException {
        final String body = loadJson("get_users.json", getClass());
        expectGet()
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUsers();

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUsersWhenEmpty() throws IOException {
        final String body = loadJson("get_users_empty.json", getClass());
        expectGet()
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getUsers();

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetUsersWhenInternalServerError() throws IOException {
        final String body = loadJson("get_users_internal_server_error.json", getClass());
        expectGet()
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getUsers());

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testUpdateUser() throws IOException {
        final UserUpdateDto dto = makeTestUserUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user.json", getClass());
        expectPatch("/" + USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.updateUser(USER_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testUpdateUserWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.updateUser(USER_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot update user: is null"));
    }

    @Test
    void testUpdateUserWhenDuplicateEmail() throws IOException {
        final UserUpdateDto dto = makeTestUserUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user_duplicate_email.json", getClass());
        expectPatch("/" + USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateUser(USER_ID, dto));

        assertThat(exception, isConflict(body));
    }

    @Test
    void testUpdateUserWhenInternalServerError() throws IOException {
        final UserUpdateDto dto = makeTestUserUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_user_internal_server_error.json", getClass());
        expectPatch("/" + USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateUser(USER_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testDeleteUser() {
        expectDelete("/" + USER_ID)
                .andRespond(withStatus(HttpStatus.OK));

        client.deleteUser(USER_ID);
    }

    @Test
    void testDeleteUserWhenInternalServerError() throws IOException {
        final String body = loadJson("delete_user_internal_server_error.json", getClass());
        expectDelete("/" + USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.deleteUser(1L));

        assertThat(exception, isInternalServerError(body));
    }
}