package ru.practicum.shareit.item;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.item.ItemUtils.assertCommentCreateDtoEqual;
import static ru.practicum.shareit.item.ItemUtils.assertItemCreateDtoEqual;
import static ru.practicum.shareit.item.ItemUtils.assertItemUpdateDtoEqual;
import static ru.practicum.shareit.item.ItemUtils.makeTestCommentCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemCreateDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemUpdateDto;

class ItemControllerTest {

    private static final String HEADER = "X-Sharer-User-Id";

    private AutoCloseable openMocks;

    @Mock
    private ItemClient client;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Captor
    private ArgumentCaptor<ItemCreateDto> itemCreateDtoCaptured;

    @Captor
    private ArgumentCaptor<CommentCreateDto> commentCreateDtoCaptured;

    @Captor
    private ArgumentCaptor<ItemUpdateDto> itemUpdateDtoCaptured;

    @Captor
    private ArgumentCaptor<Long> userIdCaptured;

    @Captor
    private ArgumentCaptor<Long> itemIdCaptured;

    private ItemController controller;

    private Object testResponse;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        controller = new ItemController(client);
        testResponse = "test response";
        when(mockHttpRequest.getMethod()).thenReturn("POST");
        when(mockHttpRequest.getRequestURI()).thenReturn("http://somehost/home");
        when(mockHttpRequest.getQueryString()).thenReturn("value=none");
        when(mockHttpRequest.getHeader(HEADER)).thenReturn("42");
    }

    @AfterEach
    void tearDown() throws Exception {
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verify(mockHttpRequest).getHeader(HEADER);
        Mockito.verifyNoMoreInteractions(client);
        openMocks.close();
    }

    @Test
    void testCreateItem() {
        when(client.createItem(eq(1L), any(ItemCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createItem(1L, makeTestItemCreateDto(), mockHttpRequest);

        verify(client).createItem(userIdCaptured.capture(), itemCreateDtoCaptured.capture());
        assertEquals(1L, userIdCaptured.getValue());
        assertItemCreateDtoEqual(makeTestItemCreateDto(), itemCreateDtoCaptured.getValue());
        assertEquals(testResponse, actual);
    }

    @Test
    void testGetItem() {
        when(client.getItem(1L, 42L)).thenReturn(testResponse);

        final Object actual = controller.getItem(1L, 42L, mockHttpRequest);

        verify(client).getItem(1L, 42L);
        assertEquals(testResponse, actual);
    }

    @Test
    void testGetItems() {
        when(client.getItems(1L)).thenReturn(testResponse);

        final Object actual = controller.getItems(1L, mockHttpRequest);

        verify(client).getItems(1L);
        assertEquals(testResponse, actual);
    }

    @Test
    void testGetItemsWithText() {
        when(client.getItems(1L, "text")).thenReturn(testResponse);

        final Object actual = controller.getItems(1L, "text", mockHttpRequest);

        verify(client).getItems(1L, "text");
        assertEquals(testResponse, actual);
    }

    @Test
    void testAddComment() {
        when(client.addComment(eq(1L), eq(42L), any(CommentCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.addComment(1L, 42L, makeTestCommentCreateDto(), mockHttpRequest);

        verify(client).addComment(userIdCaptured.capture(), itemIdCaptured.capture(),
                commentCreateDtoCaptured.capture());
        assertEquals(1L, userIdCaptured.getValue());
        assertEquals(42L, itemIdCaptured.getValue());
        assertCommentCreateDtoEqual(makeTestCommentCreateDto(), commentCreateDtoCaptured.getValue());
        assertEquals(testResponse, actual);
    }

    @Test
    void testUpdateItem() {
        when(client.updateItem(eq(1L), eq(42L), any(ItemUpdateDto.class))).thenReturn(testResponse);

        final Object actual = controller.updateItem(1L, 42L, makeTestItemUpdateDto(), mockHttpRequest);

        verify(client).updateItem(userIdCaptured.capture(), itemIdCaptured.capture(), itemUpdateDtoCaptured.capture());
        assertEquals(1L, userIdCaptured.getValue());
        assertEquals(42L, itemIdCaptured.getValue());
        assertItemUpdateDtoEqual(makeTestItemUpdateDto(), itemUpdateDtoCaptured.getValue());
        assertEquals(testResponse, actual);
    }

    @Test
    void testDeleteItem() {
        doNothing().when(client).deleteItem(1L, 42L);

        controller.deleteItem(1L, 42L, mockHttpRequest);

        verify(client).deleteItem(1L, 42L);
    }
}