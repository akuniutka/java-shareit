package ru.practicum.shareit.item;

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
import static ru.practicum.shareit.common.ErrorResponseMatchers.isBadRequest;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isInternalServerError;
import static ru.practicum.shareit.common.ErrorResponseMatchers.isNotFound;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemUpdateDto;

@RestClientTest(ItemClient.class)
class ItemClientIT extends AbstractClientIT {

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 1L;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private ItemClient client;

    @BeforeEach
    void setUp() {
        basePath = "/items";
        mockServer.reset();
    }

    @AfterEach
    void tearDown() {
        mockServer.verify();
    }

    @Test
    void testCreateItem() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.createItem(USER_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testCreateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.createItem(USER_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot create item: is null"));
    }

    @Test
    void createItemWhenUserNotFound() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_user_not_found.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItem(USER_ID, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void createItemWhenInternalServerError() throws IOException {
        final ItemCreateDto dto = makeTestItemCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_item_internal_server_error.json", getClass());
        expectPost(USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.createItem(USER_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItem() throws IOException {
        final String body = loadJson("get_item.json", getClass());
        expectGet("/" + ITEM_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItem(USER_ID, ITEM_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemWhenNotFound() throws IOException {
        final String body = loadJson("get_item_not_found.json", getClass());
        expectGet("/" + ITEM_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItem(USER_ID, ITEM_ID));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testGetItemWhenInternalServerError() throws IOException {
        final String body = loadJson("get_item_internal_server_error.json", getClass());
        expectGet("/" + ITEM_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItem(USER_ID, ITEM_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItems() throws IOException {
        final String body = loadJson("get_items.json", getClass());
        expectGet(USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(USER_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWhenEmpty() throws IOException {
        final String body = loadJson("get_items_empty.json", getClass());
        expectGet(USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(USER_ID);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWhenInternalServerError() throws IOException {
        final String body = loadJson("get_items_internal_server_error.json", getClass());
        expectGet(USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItems(USER_ID));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testGetItemsWithText() throws IOException {
        final String text = "thing";
        final String body = loadJson("get_items_with_text.json", getClass());
        expectGet("/search?text=" + text, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(USER_ID, text);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWithTextWhenEmpty() throws IOException {
        final String text = "thing";
        final String body = loadJson("get_items_with_text_empty.json", getClass());
        expectGet("/search?text=" + text, USER_ID)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.getItems(USER_ID, text);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testGetItemsWithTextWhenInternalServerError() throws IOException {
        final String text = "thing";
        final String body = loadJson("get_items_with_text_internal_server_error.json", getClass());
        expectGet("/search?text=" + text, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.getItems(USER_ID, text));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testAddComment() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment.json", getClass());
        expectPost("/" + ITEM_ID + "/comment", USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.addComment(USER_ID, ITEM_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testAddCommentWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.addComment(USER_ID, ITEM_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot create comment: is null"));
    }

    @Test
    void testAddCommentWhenBookingNotFound() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment_booking_not_found.json", getClass());
        expectPost("/" + ITEM_ID + "/comment", USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.BAD_REQUEST)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.addComment(USER_ID, ITEM_ID, dto));

        assertThat(exception, isBadRequest(body));
    }

    @Test
    void testAddCommentWhenInternalServerError() throws IOException {
        final CommentCreateDto dto = makeTestCommentCreateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("create_comment_internal_server_error.json", getClass());
        expectPost("/" + ITEM_ID + "/comment", USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.addComment(USER_ID, ITEM_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testUpdateItem() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item.json", getClass());
        expectPatch("/" + ITEM_ID, USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body));

        final Object response = client.updateItem(USER_ID, ITEM_ID, dto);

        assertThat(response, equalToJson(body));
    }

    @Test
    void testUpdateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> client.updateItem(USER_ID, ITEM_ID, null));

        assertThat(exception.getMessage(), equalTo("Cannot update item: is null"));
    }

    @Test
    void testUpdateItemWhenNotFound() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item_not_found.json", getClass());
        expectPatch("/" + ITEM_ID, USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.NOT_FOUND)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateItem(USER_ID, ITEM_ID, dto));

        assertThat(exception, isNotFound(body));
    }

    @Test
    void testUpdateItemWhenInternalServerError() throws IOException {
        final ItemUpdateDto dto = makeTestItemUpdateDto();
        final String dtoJson = mapper.writeValueAsString(dto);
        final String body = loadJson("update_item_internal_server_error.json", getClass());
        expectPatch("/" + ITEM_ID, USER_ID, dtoJson)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.updateItem(USER_ID, ITEM_ID, dto));

        assertThat(exception, isInternalServerError(body));
    }

    @Test
    void testDeleteItem() {
        expectDelete("/" + ITEM_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.OK));

        client.deleteItem(USER_ID, ITEM_ID);
    }

    @Test
    void testDeleteItemWhenInternalServerError() throws IOException {
        final String body = loadJson("delete_item_internal_server_error.json", getClass());
        expectDelete("/" + ITEM_ID, USER_ID)
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
                        .body(body));

        final HttpStatusCodeException exception = assertThrows(HttpStatusCodeException.class,
                () -> client.deleteItem(USER_ID, ITEM_ID));

        assertThat(exception, isInternalServerError(body));
    }
}