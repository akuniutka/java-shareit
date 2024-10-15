package ru.practicum.shareit.item;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.common.CommonUtils.deepEqualTo;
import static ru.practicum.shareit.common.CommonUtils.makeTestNewComment;
import static ru.practicum.shareit.common.CommonUtils.makeTestNewItem;
import static ru.practicum.shareit.common.CommonUtils.makeTestSavedComment;
import static ru.practicum.shareit.common.CommonUtils.makeTestSavedItem;
import static ru.practicum.shareit.item.ItemUtils.deepEqualTo;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentRetrieveDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemRetrieveDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemUpdateDto;

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

    @Captor
    private ArgumentCaptor<ItemCreateDto> itemCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<ItemUpdateDto> itemUpdateDtoCaptor;

    @Captor
    private ArgumentCaptor<Item> itemCaptor;

    @Captor
    private ArgumentCaptor<List<Item>> itemsCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    @Captor
    private ArgumentCaptor<Long> itemIdCaptor;

    @Captor
    private ArgumentCaptor<CommentCreateDto> commentCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<Comment> commentCaptor;

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
        when(mockMapper.mapToItem(anyLong(), any(ItemCreateDto.class))).thenReturn(makeTestNewItem());
        when(mockService.createItem(any(Item.class))).thenReturn(makeTestSavedItem());
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(makeTestItemRetrieveDto());

        final ItemRetrieveDto actual = controller.createItem(USER_ID, makeTestItemCreateDto(), mockHttpRequest);

        inOrder.verify(mockMapper).mapToItem(userIdCaptor.capture(), itemCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemCreateDtoCaptor.getValue(), deepEqualTo(makeTestItemCreateDto()));
        inOrder.verify(mockService).createItem(itemCaptor.capture());
        assertThat(itemCaptor.getValue(), deepEqualTo(makeTestNewItem()));
        inOrder.verify(mockMapper).mapToDto(itemCaptor.capture());
        assertThat(itemCaptor.getValue(), deepEqualTo(makeTestSavedItem()));
        assertThat(actual, deepEqualTo(makeTestItemRetrieveDto()));
        assertLogs(logListener.getEvents(), "create_item.json", getClass());
    }

    @Test
    void testGetItem() throws JSONException, IOException {
        when(mockService.getItem(ITEM_ID, USER_ID)).thenReturn(makeTestSavedItem());
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(makeTestItemRetrieveDto());

        final ItemRetrieveDto actual = controller.getItem(USER_ID, ITEM_ID, mockHttpRequest);

        inOrder.verify(mockService).getItem(ITEM_ID, USER_ID);
        inOrder.verify(mockMapper).mapToDto(itemCaptor.capture());
        assertThat(itemCaptor.getValue(), deepEqualTo(makeTestSavedItem()));
        assertThat(actual, deepEqualTo(makeTestItemRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_item.json", getClass());
    }

    @Test
    void testGetItems() throws JSONException, IOException {
        when(mockService.getItems(USER_ID)).thenReturn(List.of(makeTestSavedItem()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestItemRetrieveDto()));

        final List<ItemRetrieveDto> actual = controller.getItems(USER_ID, mockHttpRequest);

        inOrder.verify(mockService).getItems(USER_ID);
        inOrder.verify(mockMapper).mapToDto(itemsCaptor.capture());
        assertThat(itemsCaptor.getValue(), notNullValue());
        assertThat(itemsCaptor.getValue().size(), equalTo(1));
        assertThat(itemsCaptor.getValue().getFirst(), deepEqualTo(makeTestSavedItem()));
        assertThat(actual, notNullValue());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.getFirst(), deepEqualTo(makeTestItemRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_items.json", getClass());
    }

    @Test
    void testGetItemsWithText() throws JSONException, IOException {
        when(mockService.getItems(SEARCH_TEXT, USER_ID)).thenReturn(List.of(makeTestSavedItem()));
        when(mockMapper.mapToDto(anyList())).thenReturn(List.of(makeTestItemRetrieveDto()));

        final List<ItemRetrieveDto> actual = controller.getItems(USER_ID, SEARCH_TEXT, mockHttpRequest);

        inOrder.verify(mockService).getItems(SEARCH_TEXT, USER_ID);
        inOrder.verify(mockMapper).mapToDto(itemsCaptor.capture());
        assertThat(itemsCaptor.getValue(), notNullValue());
        assertThat(itemsCaptor.getValue().size(), equalTo(1));
        assertThat(itemsCaptor.getValue().getFirst(), deepEqualTo(makeTestSavedItem()));
        assertThat(actual, notNullValue());
        assertThat(actual.size(), equalTo(1));
        assertThat(actual.getFirst(), deepEqualTo(makeTestItemRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_items_with_text.json", getClass());
    }

    @Test
    void testAddComment() throws JSONException, IOException {
        when(commentMapper.mapToComment(anyLong(), anyLong(), any(CommentCreateDto.class)))
                .thenReturn(makeTestNewComment());
        when(commentService.addComment(any(Comment.class))).thenReturn(makeTestSavedComment());
        when(commentMapper.mapToDto(any(Comment.class))).thenReturn(makeTestCommentRetrieveDto());

        final CommentRetrieveDto actual = controller.addComment(USER_ID, ITEM_ID, makeTestCommentCreateDto(),
                mockHttpRequest);

        inOrder.verify(commentMapper).mapToComment(userIdCaptor.capture(), itemIdCaptor.capture(),
                commentCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemIdCaptor.getValue(), equalTo(ITEM_ID));
        assertThat(commentCreateDtoCaptor.getValue(), deepEqualTo(makeTestCommentCreateDto()));
        inOrder.verify(commentService).addComment(commentCaptor.capture());
        assertThat(commentCaptor.getValue(), deepEqualTo(makeTestNewComment()));
        inOrder.verify(commentMapper).mapToDto(commentCaptor.capture());
        assertThat(commentCaptor.getValue(), deepEqualTo(makeTestSavedComment()));
        assertThat(actual, deepEqualTo(makeTestCommentRetrieveDto()));
        assertLogs(logListener.getEvents(), "add_comment.json", getClass());
    }

    @Test
    void testUpdateItem() throws JSONException, IOException {
        when(mockMapper.mapToItem(any(ItemUpdateDto.class))).thenReturn(makeTestNewItem());
        when(mockService.updateItem(anyLong(), any(Item.class), anyLong())).thenReturn(makeTestSavedItem());
        when(mockMapper.mapToDto(any(Item.class))).thenReturn(makeTestItemRetrieveDto());

        final ItemRetrieveDto actual = controller.updateItem(USER_ID, ITEM_ID, makeTestItemUpdateDto(),
                mockHttpRequest);

        inOrder.verify(mockMapper).mapToItem(itemUpdateDtoCaptor.capture());
        assertThat(itemUpdateDtoCaptor.getValue(), deepEqualTo(makeTestItemUpdateDto()));
        inOrder.verify(mockService).updateItem(itemIdCaptor.capture(), itemCaptor.capture(), userIdCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemIdCaptor.getValue(), equalTo(ITEM_ID));
        assertThat(itemCaptor.getValue(), deepEqualTo(makeTestNewItem()));
        inOrder.verify(mockMapper).mapToDto(itemCaptor.capture());
        assertThat(itemCaptor.getValue(), deepEqualTo(makeTestSavedItem()));
        assertThat(actual, deepEqualTo(makeTestItemRetrieveDto()));
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