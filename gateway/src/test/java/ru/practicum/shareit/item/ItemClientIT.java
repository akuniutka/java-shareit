package ru.practicum.shareit.item;

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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static ru.practicum.shareit.common.CommonUtils.loadJson;
import static ru.practicum.shareit.common.EqualToJson.equalToJson;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isBadRequest;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemUpdateDto;

@RestClientTest(ItemClient.class)
class ItemClientIT {

    private static final String HEADER = "X-Sharer-User-Id";

    @Value("${shareit-server.url}")
    private String serverUrl;

    private String baseUrl;

    @Autowired
    private MockRestServiceServer server;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemClient client;

    @BeforeEach
    void setUp() {
        baseUrl = serverUrl + "/items";
        server.reset();
    }

    @AfterEach
    void tearDown() {
        server.verify();
    }

    @Test
    void testCreateItem() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final long userId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createItem(userId, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createItem(1L, null));

        assertThat(exception.getMessage(), equalTo("Cannot create item: is null"));
    }

    @Test
    void createItemWhenUserNotFound() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final long userId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_user_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItem(userId, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void createItemWhenInternalServerError() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final long userId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItem(userId, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItem() throws IOException {
        final long userId = 42L;
        final long itemId = 1L;
        final String body = loadJson("get_item.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItem(userId, itemId);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemWhenNotFound() throws IOException {
        final long userId = 42L;
        final long itemId = 1L;
        final String body = loadJson("get_item_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItem(userId, itemId));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetItemWhenInternalServerError() throws IOException {
        final long userId = 42L;
        final long itemId = 1L;
        final String body = loadJson("get_item_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItem(userId, itemId));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItems() throws IOException {
        final long userId = 42L;
        final String body = loadJson("get_items.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(userId);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWhenEmpty() throws IOException {
        final long userId = 42L;
        final String body = loadJson("get_items_empty.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(userId);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWhenInternalServerError() throws IOException {
        final long userId = 42L;
        final String body = loadJson("get_items_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItems(userId));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItemsWithText() throws IOException {
        final long userId = 42L;
        final String text = "thing";
        final String body = loadJson("get_items_with_text.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/search?text=" + text))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(userId, text);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWithTextWhenEmpty() throws IOException {
        final long userId = 42L;
        final String text = "thing";
        final String body = loadJson("get_items_with_text_empty.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/search?text=" + text))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(userId, text);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWithTextWhenInternalServerError() throws IOException {
        final long userId = 42L;
        final String text = "thing";
        final String body = loadJson("get_items_with_text_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/search?text=" + text))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItems(userId, text));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testAddComment() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.addComment(userId, itemId, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testAddCommentWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.addComment(42L, 1L, null));

        assertThat(exception.getMessage(), equalTo("Cannot create comment: is null"));
    }

    @Test
    void testAddCommentWhenBookingNotFound() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment_booking_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.addComment(userId, itemId, dto));

        assertThat(exception, isBadRequest(body));
    }

    @Test
    void testAddCommentWhenInternalServerError() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId + "/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.addComment(userId, itemId, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testUpdateItem() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.updateItem(userId, itemId, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testUpdateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.updateItem(42L, 1L, null));

        assertThat(exception.getMessage(), equalTo("Cannot update item: is null"));
    }

    @Test
    void testUpdateItemWhenNotFound() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item_not_found.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateItem(userId, itemId, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testUpdateItemWhenInternalServerError() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final long userId = 42L;
        final long itemId = 1L;
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(dtoJson, true))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateItem(userId, itemId, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testDeleteItem() {
        final long userId = 42L;
        final long itemId = 1L;
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.OK));

        client.deleteItem(userId, itemId);
    }

    @Test
    void testDeleteItemWhenInternalServerError() throws IOException {
        final long userId = 42L;
        final long itemId = 1L;
        final String body = loadJson("delete_item_internal_server_error.json", getClass());
        server.expect(ExpectedCount.once(), requestTo(baseUrl + "/" + itemId))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andExpect(header(HEADER, String.valueOf(userId)))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.deleteItem(userId, itemId));

        assertThat(exception, isInternalServerError(body));
    }
}