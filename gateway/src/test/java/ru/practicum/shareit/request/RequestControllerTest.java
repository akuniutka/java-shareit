package ru.practicum.shareit.request;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import ru.practicum.shareit.common.AbstractControllerTest;
import ru.practicum.shareit.common.LogListener;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestCreateDto;

class RequestControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(RequestController.class);

    private static final long REQUEST_ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Mock
    private RequestClient client;

    private RequestController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new RequestController(client);
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
    void testCreateRequest() throws JSONException, IOException {
        when(client.createRequest(USER_ID, makeTestRequestCreateDto())).thenReturn(testResponse);

        final Object actual = controller.createRequest(USER_ID, makeTestRequestCreateDto(), mockHttpRequest);

        verify(client).createRequest(USER_ID, makeTestRequestCreateDto());
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "create_request.json", getClass());
    }

    @Test
    void testGetRequest() throws JSONException, IOException {
        when(client.getRequest(USER_ID, REQUEST_ID)).thenReturn(testResponse);

        final Object actual = controller.getRequest(USER_ID, REQUEST_ID, mockHttpRequest);

        verify(client).getRequest(USER_ID, REQUEST_ID);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_request.json", getClass());
    }

    @Test
    void testGetOwnRequests() throws JSONException, IOException {
        when(client.getOwnRequests(USER_ID, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getOwnRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        verify(client).getOwnRequests(USER_ID, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_own_requests.json", getClass());
    }

    @Test
    void testGetOtherRequests() throws JSONException, IOException {
        when(client.getOthersRequests(USER_ID, FROM, SIZE)).thenReturn(testResponse);

        final Object actual = controller.getOthersRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        verify(client).getOthersRequests(USER_ID, FROM, SIZE);
        assertThat(actual, equalTo(testResponse));
        assertLogs(logListener.getEvents(), "get_others_requests.json", getClass());
    }
}