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
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

@RestClientTest(UserClient.class)
class UserClientTest {

    @Value("${shareit-server.url}")
    private String serverUrl;

    @Autowired
    private UserClient client;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @BeforeEach
    void setUp() {
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
        final File resource = new ClassPathResource("create_user.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.createUser(dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testCreateUserWhenDuplicateEmail() throws IOException, JSONException {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("create_user_duplicate_email.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.createUser(dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testCreateUserWhenInternalServerError() throws IOException, JSONException {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("create_user_internal_server_error.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.createUser(dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUser() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_user.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUser(1L);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUserWhenNotFound() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_user_not_found.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUser(1L);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUserWhenInternalServerError() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_user_internal_server_error.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUser(1L);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUsers() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_users.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUsers();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUsersWhenEmpty() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_users_empty.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUsers();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testGetUsersWhenInternalServerError() throws IOException, JSONException {
        final File resource = new ClassPathResource("get_users_internal_server_error.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.getUsers();

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testUpdateUser() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("update_user.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.updateUser(1L, dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testUpdateUserWhenDuplicateEmail() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("update_user_duplicate_email.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.CONFLICT)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.updateUser(1L, dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.CONFLICT));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testUpdateUserWhenNotFound() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("update_user_not_found.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.updateUser(1L, dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.NOT_FOUND));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testUpdateUserWhenInternalServerError() throws IOException, JSONException {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        final String dtoJson = mapper.writeValueAsString(dto);
        final File resource = new ClassPathResource("update_user_internal_server_error.json", getClass()).getFile();
        final String body = Files.readString(resource.toPath());
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final ResponseEntity<Object> response = client.updateUser(1L, dto);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.INTERNAL_SERVER_ERROR));
        assertThat(response.getHeaders().getContentType(), equalTo(MediaType.APPLICATION_PROBLEM_JSON));
        final String actual = mapper.writeValueAsString(response.getBody());
        JSONAssert.assertEquals(actual, body, false);
    }

    @Test
    void testDeleteUser() {
        server.expect(ExpectedCount.once(), requestTo(serverUrl + "/users/1"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withStatus(HttpStatus.OK));

        client.deleteUser(1L);
    }
}