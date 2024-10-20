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
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequest;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestCreateDto;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestRetrieveDto;

class RequestControllerTest extends AbstractControllerTest {

    private static final LogListener logListener = new LogListener(RequestController.class);

    private static final int FROM = 0;
    private static final int SIZE = 10;

    @Mock
    private RequestService mockService;

    @Mock
    private RequestMapper mockMapper;

    private InOrder inOrder;

    private RequestController controller;

    @Override
    @BeforeEach
    protected void setUp() {
        super.setUp();
        controller = new RequestController(mockService, mockMapper);
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
    void testCreateRequest() throws JSONException, IOException {
        when(mockMapper.mapToRequest(USER_ID, makeTestRequestCreateDto())).thenReturn(makeTestRequest().withNoId());
        when(mockService.createRequest(makeTestRequest().withNoId())).thenReturn(makeTestRequest());
        when(mockMapper.mapToDto(makeTestRequest())).thenReturn(makeTestRequestRetrieveDto());

        final RequestRetrieveDto actual = controller.createRequest(USER_ID, makeTestRequestCreateDto(),
                mockHttpRequest);

        inOrder.verify(mockMapper).mapToRequest(USER_ID, makeTestRequestCreateDto());
        inOrder.verify(mockService).createRequest(makeTestRequest().withNoId());
        inOrder.verify(mockMapper).mapToDto(makeTestRequest());
        assertThat(actual, equalTo(makeTestRequestRetrieveDto()));
        assertLogs(logListener.getEvents(), "create_request.json", getClass());
    }

    @Test
    void testGetRequest() throws JSONException, IOException {
        when(mockService.getRequestWithRelations(REQUEST_ID, USER_ID)).thenReturn(makeTestRequest());
        when(mockMapper.mapToDto(makeTestRequest())).thenReturn(makeTestRequestRetrieveDto());

        final RequestRetrieveDto actual = controller.getRequest(USER_ID, REQUEST_ID, mockHttpRequest);

        inOrder.verify(mockService).getRequestWithRelations(REQUEST_ID, USER_ID);
        inOrder.verify(mockMapper).mapToDto(makeTestRequest());
        assertThat(actual, equalTo(makeTestRequestRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_request.json", getClass());
    }

    @Test
    void testGetOwnRequests() throws JSONException, IOException {
        when(mockService.getOwnRequests(USER_ID, FROM, SIZE)).thenReturn(List.of(makeTestRequest()));
        when(mockMapper.mapToDto(List.of(makeTestRequest()))).thenReturn(List.of(makeTestRequestRetrieveDto()));

        final List<RequestRetrieveDto> actual = controller.getOwnRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getOwnRequests(USER_ID, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestRequest()));
        assertThat(actual, contains(makeTestRequestRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_own_requests.json", getClass());
    }

    @Test
    void testGetOthersRequests() throws JSONException, IOException {
        when(mockService.getOthersRequests(USER_ID, FROM, SIZE)).thenReturn(List.of(makeTestRequest()));
        when(mockMapper.mapToDto(List.of(makeTestRequest()))).thenReturn(List.of(makeTestRequestRetrieveDto()));

        final List<RequestRetrieveDto> actual = controller.getOthersRequests(USER_ID, FROM, SIZE, mockHttpRequest);

        inOrder.verify(mockService).getOthersRequests(USER_ID, FROM, SIZE);
        inOrder.verify(mockMapper).mapToDto(List.of(makeTestRequest()));
        assertThat(actual, contains(makeTestRequestRetrieveDto()));
        assertLogs(logListener.getEvents(), "get_others_requests.json", getClass());
    }
}