package ru.practicum.shareit.item;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.item.ItemUtils.makeCommentCreateDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeCommentProxy;
import static ru.practicum.shareit.item.ItemUtils.makeCommentRetrieveDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemCreateDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemRetrieveDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemUpdateDtoProxy;

class ItemControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(ItemController.class);

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 1L;
    private static final String SEARCH_TEXT = "text";

    @Mock
    private ItemService mockService;

    @Mock
    private ItemMapper mockMapper;

    @Mock
    private CommentService commentService;

    @Mock
    private CommentMapper commentMapper;

    private InOrder inOrder;

    private ItemController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new ItemController(mockService, mockMapper, commentService, commentMapper);
        logListener.startListen();
        logListener.reset();
        inOrder = inOrder(mockService, mockMapper, commentService, commentMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper, commentService, commentMapper);
        super.tearDown();
    }

    @Test
    void textCreateItem() throws JSONException, IOException {
        when(mockMapper.mapToItem(USER_ID, makeItemCreateDtoProxy())).thenReturn(makeItemProxy());
        when(mockService.createItem(makeItemProxy())).thenReturn(makeItemProxy());
        when(mockMapper.mapToDto(makeItemProxy())).thenReturn(makeItemRetrieveDtoProxy());

        final ItemRetrieveDto actual = controller.createItem(USER_ID, makeItemCreateDtoProxy(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToItem(USER_ID, makeItemCreateDtoProxy());
        inOrder.verify(mockService).createItem(makeItemProxy());
        inOrder.verify(mockMapper).mapToDto(makeItemProxy());
        assertThat(actual, equalTo(makeItemRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "create_item.json", getClass());
    }

    @Test
    void testGetItem() throws JSONException, IOException {
        when(mockService.getItem(ITEM_ID, USER_ID)).thenReturn(makeItemProxy());
        when(mockMapper.mapToDto(makeItemProxy())).thenReturn(makeItemRetrieveDtoProxy());

        final ItemRetrieveDto actual = controller.getItem(USER_ID, ITEM_ID, mockHttpRequest);

        inOrder.verify(mockService).getItem(ITEM_ID, USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeItemProxy());
        assertThat(actual, equalTo(makeItemRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_item.json", getClass());
    }

    @Test
    void testGetItems() throws JSONException, IOException {
        when(mockService.getItems(USER_ID)).thenReturn(List.of(makeItemProxy()));
        when(mockMapper.mapToDto(List.of(makeItemProxy()))).thenReturn(List.of(makeItemRetrieveDtoProxy()));

        final List<ItemRetrieveDto> actual = controller.getItems(USER_ID, mockHttpRequest);

        inOrder.verify(mockService).getItems(USER_ID);
        inOrder.verify(mockMapper).mapToDto(List.of(makeItemProxy()));
        assertThat(actual, contains(makeItemRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_items.json", getClass());
    }

    @Test
    void testGetItemsWithText() throws JSONException, IOException {
        when(mockService.getItems(SEARCH_TEXT, USER_ID)).thenReturn(List.of(makeItemProxy()));
        when(mockMapper.mapToDto(List.of(makeItemProxy()))).thenReturn(List.of(makeItemRetrieveDtoProxy()));

        final List<ItemRetrieveDto> actual = controller.getItems(USER_ID, SEARCH_TEXT, mockHttpRequest);

        inOrder.verify(mockService).getItems(SEARCH_TEXT, USER_ID);
        inOrder.verify(mockMapper).mapToDto(List.of(makeItemProxy()));
        assertThat(actual, contains(makeItemRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_items_with_text.json", getClass());
    }

    @Test
    void testAddComment() throws JSONException, IOException {
        when(commentMapper.mapToComment(USER_ID, ITEM_ID, makeCommentCreateDtoProxy())).thenReturn(makeCommentProxy());
        when(commentService.addComment(makeCommentProxy())).thenReturn(makeCommentProxy());
        when(commentMapper.mapToDto(makeCommentProxy())).thenReturn(makeCommentRetrieveDtoProxy());

        final CommentRetrieveDto actual = controller.addComment(USER_ID, ITEM_ID, makeCommentCreateDtoProxy(),
                mockHttpRequest);

        inOrder.verify(commentMapper).mapToComment(USER_ID, ITEM_ID, makeCommentCreateDtoProxy());
        inOrder.verify(commentService).addComment(makeCommentProxy());
        inOrder.verify(commentMapper).mapToDto(makeCommentProxy());
        assertThat(actual, equalTo(makeCommentRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "add_comment.json", getClass());
    }

    @Test
    void testUpdateItem() throws JSONException, IOException {
        when(mockMapper.mapToItem(makeItemUpdateDtoProxy())).thenReturn(makeItemProxy());
        when(mockService.updateItem(ITEM_ID, makeItemProxy(), USER_ID)).thenReturn(makeItemProxy());
        when(mockMapper.mapToDto(makeItemProxy())).thenReturn(makeItemRetrieveDtoProxy());

        final ItemRetrieveDto actual = controller.updateItem(USER_ID, ITEM_ID, makeItemUpdateDtoProxy(),
                mockHttpRequest);

        inOrder.verify(mockMapper).mapToItem(makeItemUpdateDtoProxy());
        inOrder.verify(mockService).updateItem(ITEM_ID, makeItemProxy(), USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeItemProxy());
        assertThat(actual, equalTo(makeItemRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "update_item.json", getClass());
    }

    @Test
    void testDeleteItem() throws JSONException, IOException {
        doNothing().when(mockService).deleteItem(ITEM_ID, USER_ID);

        controller.deleteItem(USER_ID, ITEM_ID, mockHttpRequest);

        verify(mockService).deleteItem(ITEM_ID, USER_ID);
        assertLogs(logListener.getEvents(), "delete_item.json", getClass());
    }
}