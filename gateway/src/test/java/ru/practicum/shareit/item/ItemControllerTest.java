package ru.practicum.shareit.item;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.item.ItemUtils.deepEqualTo;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemUpdateDto;

class ItemControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(ItemController.class);

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 1L;
    private static final String SEARCH_TEXT = "text";

    @Mock
    private ItemClient client;

    @Captor
    private ArgumentCaptor<ItemCreateDto> itemCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<CommentCreateDto> commentCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<ItemUpdateDto> itemUpdateDtoCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<Long> itemIdCaptor;

    private ItemController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new ItemController(client);
        logListener.startListen();
        logListener.reset();
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(client);
        super.tearDown();
    }

    @Test
    void testCreateItem() throws JSONException, IOException {
        when(client.createItem(anyLong(), any(ItemCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createItem(USER_ID, makeTestItemCreateDto(), mockHttpRequest);

        verify(client).createItem(userIdCaptor.capture(), itemCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemCreateDtoCaptor.getValue(), deepEqualTo(makeTestItemCreateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "create_item.json", getClass());
    }

    @Test
    void testGetItem() throws JSONException, IOException {
        when(client.getItem(USER_ID, ITEM_ID)).thenReturn(testResponse);

        final Object actual = controller.getItem(USER_ID, ITEM_ID, mockHttpRequest);

        verify(client).getItem(USER_ID, ITEM_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_item.json", getClass());
    }

    @Test
    void testGetItems() throws JSONException, IOException {
        when(client.getItems(USER_ID)).thenReturn(testResponse);

        final Object actual = controller.getItems(USER_ID, mockHttpRequest);

        verify(client).getItems(USER_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_items.json", getClass());
    }

    @Test
    void testGetItemsWithText() throws JSONException, IOException {
        when(client.getItems(USER_ID, SEARCH_TEXT)).thenReturn(testResponse);

        final Object actual = controller.getItems(USER_ID, SEARCH_TEXT, mockHttpRequest);

        verify(client).getItems(USER_ID, SEARCH_TEXT);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_items_with_test.json", getClass());
    }

    @Test
    void testAddComment() throws JSONException, IOException {
        when(client.addComment(anyLong(), anyLong(), any(CommentCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.addComment(USER_ID, ITEM_ID, makeTestCommentCreateDto(), mockHttpRequest);

        verify(client).addComment(userIdCaptor.capture(), itemIdCaptor.capture(), commentCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemIdCaptor.getValue(), equalTo(ITEM_ID));
        assertThat(commentCreateDtoCaptor.getValue(), deepEqualTo(makeTestCommentCreateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "add_comment.json", getClass());
    }

    @Test
    void testUpdateItem() throws JSONException, IOException {
        when(client.updateItem(anyLong(), anyLong(), any(ItemUpdateDto.class))).thenReturn(testResponse);

        final Object actual = controller.updateItem(USER_ID, ITEM_ID, makeTestItemUpdateDto(), mockHttpRequest);

        verify(client).updateItem(userIdCaptor.capture(), itemIdCaptor.capture(), itemUpdateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemIdCaptor.getValue(), equalTo(ITEM_ID));
        assertThat(itemUpdateDtoCaptor.getValue(), deepEqualTo(makeTestItemUpdateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "update_item.json", getClass());
    }

    @Test
    void testDeleteItem() throws JSONException, IOException {
        doNothing().when(client).deleteItem(USER_ID, ITEM_ID);

        controller.deleteItem(USER_ID, ITEM_ID, mockHttpRequest);

        verify(client).deleteItem(USER_ID, ITEM_ID);
        assertLogs(logListener.getEvents(), "delete_item.json", getClass());
    }
}