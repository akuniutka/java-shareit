package ru.practicum.shareit.user;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    private ObjectMapper mapper;

    @Value("${shareit-server.url}")
    private String serverUrl;

    @MockBean
    private UserClient client;

    @Autowired
    private MockMvc mvc;

    private UserCreateDto userCreateDto;
    private UserUpdateDto userUpdateDto;
    private String baseUrl;

    @BeforeEach
    void setUp() {
        Mockito.reset(client);
        baseUrl = serverUrl + "/users";
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("John Doe");
        userCreateDto.setEmail("john_doe@mail.com");
        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setName("John Doe");
        userUpdateDto.setEmail("john_doe@mail.com");
    }

    @AfterEach
    void tearDown() {
        Mockito.verifyNoMoreInteractions(client);
    }

    @Test
    void testCreateUser() throws Exception {
        final String body = loadJson("create_user.json");
        when(client.createUser(userCreateDto)).thenReturn(makeOkResponse(body));

        mvc.perform(post(baseUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(body, true));

        verify(client).createUser(userCreateDto);
    }

    @Test
    void testCreateUserWhenDuplicateEmail() throws Exception {
        final String body = loadJson("create_user_duplicate_email.json");
        when(client.createUser(userCreateDto)).thenReturn(makeErrorResponse(HttpStatus.CONFLICT, body));

        mvc.perform(post(baseUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));

        verify(client).createUser(userCreateDto);
    }

    @Test
    void testCreateUserWhenNoEmail() throws Exception {
        userCreateDto.setEmail(null);
        final String body = loadJson("create_user_no_email.json");

        mvc.perform(post(baseUrl)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userCreateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));
    }

    @Test
    void testGetUser() throws Exception {
        final String body = loadJson("get_user.json");
        when(client.getUser(1L)).thenReturn(makeOkResponse(body));

        mvc.perform(get(baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(body, true));

        verify(client).getUser(1L);
    }

    @Test
    void testGetUserWhenNotFound() throws Exception {
        final String body = loadJson("get_user_not_found.json");
        when(client.getUser(1L)).thenReturn(makeErrorResponse(HttpStatus.NOT_FOUND, body));

        mvc.perform(get(baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));

        verify(client).getUser(1L);
    }

    @Test
    void testGetUserWhenWrongIdFormat() throws Exception {
        final String body = loadJson("get_user_wrong_id_format.json");

        mvc.perform(get(baseUrl + "/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));
    }

    @Test
    void testGetUsers() throws Exception {
        final String body = loadJson("get_users.json");
        when(client.getUsers()).thenReturn(makeOkResponse(body));

        mvc.perform(get(baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(body, true));

        verify(client).getUsers();
    }

    @Test
    void testGetUsersWhenEmpty() throws Exception {
        final String body = loadJson("get_users_empty.json");
        when(client.getUsers()).thenReturn(makeOkResponse(body));

        mvc.perform(get(baseUrl)
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(body, true));

        verify(client).getUsers();
    }

    @Test
    void testUpdateUser() throws Exception {
        final String body = loadJson("update_user.json");
        when(client.updateUser(1L, userUpdateDto)).thenReturn(makeOkResponse(body));

        mvc.perform(patch(baseUrl + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json(body, true));

        verify(client).updateUser(1L, userUpdateDto);
    }

    @Test
    void testUpdateUserWhenMalformedEmail() throws Exception {
        userUpdateDto.setEmail("malformed_email");
        final String body = loadJson("update_user_malformed_email.json");

        mvc.perform(patch(baseUrl + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isBadRequest(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));
    }

    @Test
    void testUpdateUserWhenNotFound() throws Exception {
        final String body = loadJson("update_user_not_found.json");
        when(client.updateUser(1L, userUpdateDto)).thenReturn(makeErrorResponse(HttpStatus.NOT_FOUND, body));

        mvc.perform(patch(baseUrl + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isNotFound(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));

        verify(client).updateUser(1L, userUpdateDto);
    }

    @Test
    void testUpdateUserWhenDuplicateEmail() throws Exception {
        final String body = loadJson("update_user_duplicate_email.json");
        when(client.updateUser(1L, userUpdateDto)).thenReturn(makeErrorResponse(HttpStatus.CONFLICT, body));

        mvc.perform(patch(baseUrl + "/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isConflict(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));

        verify(client).updateUser(1L, userUpdateDto);
    }

    @Test
    void testUpdateUserWhenWrongIdFormat() throws Exception {
        final String body = loadJson("update_user_wrong_id_format.json");

        mvc.perform(patch(baseUrl + "/abc")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(userUpdateDto)))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));
    }

    @Test
    void testDeleteUser() throws Exception {
        doNothing().when(client).deleteUser(1L);

        mvc.perform(delete(baseUrl + "/1")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());

        verify(client).deleteUser(1L);
    }

    @Test
    void testDeleteUserWhenWrongIdFormat() throws Exception {
        final String body = loadJson("delete_user_wrong_id_format.json");

        mvc.perform(delete(baseUrl + "/abc")
                        .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpectAll(
                        status().isInternalServerError(),
                        content().contentType(MediaType.APPLICATION_PROBLEM_JSON),
                        content().json(body, true));
    }

    private String loadJson(final String filename) throws IOException {
        final ClassPathResource resource = new ClassPathResource(filename, getClass());
        return Files.readString(resource.getFile().toPath());
    }

    private ResponseEntity<Object> makeOkResponse(final String body) throws JsonProcessingException {
        return makeResponse(HttpStatus.OK, MediaType.APPLICATION_JSON, body);
    }

    private ResponseEntity<Object> makeErrorResponse(final HttpStatus status, final String body) throws
            JsonProcessingException {
        return makeResponse(status, MediaType.APPLICATION_PROBLEM_JSON, body);
    }

    private ResponseEntity<Object> makeResponse(final HttpStatus status, final MediaType contentType,
            final String body
    ) throws JsonProcessingException {
        return ResponseEntity
                .status(status)
                .contentType(contentType)
                .body(mapper.readValue(body, Object.class));
    }
}
