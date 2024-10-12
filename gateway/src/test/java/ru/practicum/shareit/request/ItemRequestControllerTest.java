package ru.practicum.shareit.request;

import jakarta.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.ItemRequestUtils.assertItemRequestCreateDtoEqual;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequestCreateDto;

class ItemRequestControllerTest {

    private static final LogListener logListener = new LogListener(ItemRequestController.class);

    private static final long USER_ID = 42L;
    private static final long REQUEST_ID = 1L;
    private static final String HEADER = "X-Sharer-User-Id";

    private AutoCloseable openMocks;

    @Mock
    private ItemRequestClient client;

    @Mock
    private HttpServletRequest mockHttpRequest;

    @Captor
    private ArgumentCaptor<ItemRequestCreateDto> itemRequestCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private ItemRequestController controller;

    private Object testResponse;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        controller = new ItemRequestController(client);
        testResponse = "test response";
        when(mockHttpRequest.getMethod()).thenReturn("POST");
        when(mockHttpRequest.getRequestURI()).thenReturn("http://somehost/home");
        when(mockHttpRequest.getQueryString()).thenReturn("value=none");
        when(mockHttpRequest.getHeader(HEADER)).thenReturn(String.valueOf(USER_ID));
        logListener.startListen();
        logListener.reset();
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getMethod();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getRequestURI();
        Mockito.verify(mockHttpRequest, Mockito.times(2)).getQueryString();
        Mockito.verify(mockHttpRequest).getHeader(HEADER);
        Mockito.verifyNoMoreInteractions(client);
        openMocks.close();
    }

    @Test
    void testCreateItemRequest() throws JSONException, IOException {
        when(client.createItemRequest(eq(USER_ID), any(ItemRequestCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createItemRequest(USER_ID, makeTestItemRequestCreateDto(), mockHttpRequest);

        verify(client).createItemRequest(userIdCaptor.capture(), itemRequestCreateDtoCaptor.capture());
        assertEquals(USER_ID, userIdCaptor.getValue());
        assertItemRequestCreateDtoEqual(makeTestItemRequestCreateDto(), itemRequestCreateDtoCaptor.getValue());
        assertEquals(testResponse, actual);
        assertLogs(logListener.getEvents(), "create_item_request.json", getClass());
    }

    @Test
    void testGetItemRequest() throws JSONException, IOException {
        when(client.getItemRequest(USER_ID, REQUEST_ID)).thenReturn(testResponse);

        final Object actual = controller.getItemRequest(USER_ID, REQUEST_ID, mockHttpRequest);

        verify(client).getItemRequest(USER_ID, REQUEST_ID);
        assertEquals(testResponse, actual);
        assertLogs(logListener.getEvents(), "get_item_request.json", getClass());
    }

    @Test
    void testGetOwnItemRequests() throws JSONException, IOException {
        when(client.getOwnItemRequests(USER_ID, 0, 10)).thenReturn(testResponse);

        final Object actual = controller.getOwnItemRequests(USER_ID, 0, 10, mockHttpRequest);

        verify(client).getOwnItemRequests(USER_ID, 0, 10);
        assertEquals(testResponse, actual);
        assertLogs(logListener.getEvents(), "get_own_item_requests.json", getClass());
    }

    @Test
    void testGetOtherRequests() throws JSONException, IOException {
        when(client.getOthersItemRequests(USER_ID, 0, 10)).thenReturn(testResponse);

        final Object actual = controller.getOthersRequests(USER_ID, 0, 10, mockHttpRequest);

        verify(client).getOthersItemRequests(USER_ID, 0, 10);
        assertEquals(testResponse, actual);
        assertLogs(logListener.getEvents(), "get_others_item_requests.json", getClass());
    }
}