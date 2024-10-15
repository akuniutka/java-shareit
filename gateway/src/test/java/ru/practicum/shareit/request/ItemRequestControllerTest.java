package ru.practicum.shareit.request;

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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.ItemRequestUtils.deepEqualTo;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequestCreateDto;

class ItemRequestControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(ItemRequestController.class);

    private static final long USER_ID = 42L;
    private static final long REQUEST_ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Mock
    private ItemRequestClient client;

    @Captor
    private ArgumentCaptor<ItemRequestCreateDto> itemRequestCreateDtoCaptor;

    @Captor
    private ArgumentCaptor<Long> userIdCaptor;

    private ItemRequestController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new ItemRequestController(client);
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
    void testCreateItemRequest() throws JSONException, IOException {
        when(client.createItemRequest(anyLong(), any(ItemRequestCreateDto.class))).thenReturn(testResponse);

        final Object actual = controller.createItemRequest(USER_ID, makeTestItemRequestCreateDto(), mockHttpRequest);

        verify(client).createItemRequest(userIdCaptor.capture(), itemRequestCreateDtoCaptor.capture());
        assertThat(userIdCaptor.getValue(), equalTo(USER_ID));
        assertThat(itemRequestCreateDtoCaptor.getValue(), deepEqualTo(makeTestItemRequestCreateDto()));
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "create_item_request.json", getClass());
    }

    @Test
    void testGetItemRequest() throws JSONException, IOException {
        when(client.getItemRequest(USER_ID, REQUEST_ID)).thenReturn(testResponse);

        final Object actual = controller.getItemRequest(USER_ID, REQUEST_ID, mockHttpRequest);

        verify(client).getItemRequest(USER_ID, REQUEST_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_item_request.json", getClass());
    }

    @Test
    void testGetOwnItemRequests() throws JSONException, IOException {
        when(client.getOwnItemRequests(USER_ID, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getOwnItemRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        verify(client).getOwnItemRequests(USER_ID, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_own_item_requests.json", getClass());
    }

    @Test
    void testGetOtherRequests() throws JSONException, IOException {
        when(client.getOthersItemRequests(USER_ID, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getOthersRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        verify(client).getOthersItemRequests(USER_ID, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_others_item_requests.json", getClass());
    }
}