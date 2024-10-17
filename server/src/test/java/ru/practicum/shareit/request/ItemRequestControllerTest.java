package ru.practicum.shareit.request;

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
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestCreateDtoProxy;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestProxy;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestRetrieveDtoProxy;

class ItemRequestControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(ItemRequestController.class);

    private static final long USER_ID = 42L;
    private static final long REQUEST_ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Mock
    private ItemRequestService mockService;

    @Mock
    private ItemRequestMapper mockMapper;

    private InOrder inOrder;

    private ItemRequestController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new ItemRequestController(mockService, mockMapper);
        logListener.startListen();
        logListener.reset();
        inOrder = inOrder(mockService, mockMapper);
    }

    @Override
    @AfterEach
    protected void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockService, mockMapper);
        super.tearDown();
    }

    @Test
    void testCreateItemRequest() throws JSONException, IOException {
        when(mockMapper.mapToItemRequest(USER_ID, makeItemRequestCreateDtoProxy())).thenReturn(makeItemRequestProxy());
        when(mockService.createItemRequest(makeItemRequestProxy())).thenReturn(makeItemRequestProxy());
        when(mockMapper.mapToDto(makeItemRequestProxy())).thenReturn(makeItemRequestRetrieveDtoProxy());

        final ItemRequestRetrieveDto actual = controller.createItemRequest(USER_ID, makeItemRequestCreateDtoProxy(),
                mockHttpRequest);

        inOrder.verify(mockMapper).mapToItemRequest(USER_ID, makeItemRequestCreateDtoProxy());
        inOrder.verify(mockService).createItemRequest(makeItemRequestProxy());
        inOrder.verify(mockMapper).mapToDto(makeItemRequestProxy());
        assertThat(actual, equalTo(makeItemRequestRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "create_item_request.json", getClass());
    }

    @Test
    void testGetItemRequest() throws JSONException, IOException {
        when(mockService.getItemRequestWithRelations(REQUEST_ID, USER_ID)).thenReturn(makeItemRequestProxy());
        when(mockMapper.mapToDto(makeItemRequestProxy())).thenReturn(makeItemRequestRetrieveDtoProxy());

        final ItemRequestRetrieveDto actual = controller.getItemRequest(USER_ID, REQUEST_ID, mockHttpRequest);

        inOrder.verify(mockService).getItemRequestWithRelations(REQUEST_ID, USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeItemRequestProxy());
        assertThat(actual, equalTo(makeItemRequestRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_item_request.json", getClass());
    }

    @Test
    void testGetOwnItemRequests() throws JSONException, IOException {
        when(mockService.getOwnRequests(USER_ID, FROM, SIZE)).thenReturn(List.of(makeItemRequestProxy()));
        when(mockMapper.mapToDto(List.of(makeItemRequestProxy())))
                .thenReturn(List.of(makeItemRequestRetrieveDtoProxy()));

        final List<ItemRequestRetrieveDto> actual = controller.getOwnItemRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getOwnRequests(USER_ID, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeItemRequestProxy()));
        assertThat(actual, contains(makeItemRequestRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_own_item_requests.json", getClass());
    }

    @Test
    void testGetOthersItemRequests() throws JSONException, IOException {
        when(mockService.getOthersRequests(USER_ID, FROM, SIZE)).thenReturn(List.of(makeItemRequestProxy()));
        when(mockMapper.mapToDto(List.of(makeItemRequestProxy())))
                .thenReturn(List.of(makeItemRequestRetrieveDtoProxy()));

        final List<ItemRequestRetrieveDto> actual = controller.getOthersItemRequests(USER_ID, FROM, SIZE,
                mockHttpRequest);

        inOrder.verify(mockService).getOthersRequests(USER_ID, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeItemRequestProxy()));
        assertThat(actual, contains(makeItemRequestRetrieveDtoProxy()));
        assertLogs(logListener.getEvents(), "get_others_item_requests.json", getClass());
    }
}