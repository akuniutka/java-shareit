package ru.practicum.shareit.request;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.common.LogListener;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.RequestUtils.deepEqualTo;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequest;

class RequestServiceImplTest {

    private static final LogListener logListener = new LogListener(RequestServiceImpl.class);

    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final Pageable PAGE = PageRequest.of(FROM / SIZE, SIZE, Sort.by(Sort.Direction.DESC, "created"));

    private AutoCloseable openMocks;

    @Mock
    private RequestRepository mockRepository;

    @Mock
    private UserService userService;

    private InOrder inOrder;

    private RequestService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new RequestServiceImpl(mockRepository, userService);
        logListener.startListen();
        logListener.reset();
        inOrder = inOrder(mockRepository, userService);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, userService);
        openMocks.close();
    }

    @Test
    void testCreateRequest() throws JSONException, IOException {
        when(mockRepository.save(makeTestRequest().withNoId())).thenReturn(makeTestRequest());

        final Request actual = service.createRequest(makeTestRequest().withNoId());

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).save(makeTestRequest().withNoId());
        assertThat(actual, deepEqualTo(makeTestRequest()));
        assertLogs(logListener.getEvents(), "create_request.json", getClass());
    }

    @Test
    void testCreateRequestWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.createRequest(null));

        assertThat(exception.getMessage(), equalTo("Cannot create request: is null"));
    }

    @Test
    void testCreateRequestWhenRequestIdNull() {
        final Request request = makeTestRequest().withNoId();
        request.getRequester().setId(null);

        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.createRequest(request));

        assertThat(exception.getMessage(), equalTo("Cannot create request: requester id is null"));
    }

    @Test
    void testGetRequest() {
        when(mockRepository.findById(REQUEST_ID)).thenReturn(Optional.of(makeTestRequest()));

        final Request actual = service.getRequest(REQUEST_ID);

        verify(mockRepository).findById(REQUEST_ID);
        assertThat(actual, deepEqualTo(makeTestRequest()));
    }

    @Test
    void testGetRequestWhenNotFound() {
        when(mockRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.getRequest(REQUEST_ID));

        verify(mockRepository).findById(REQUEST_ID);
        assertThat(exception.getModelName(), equalTo("request"));
        assertThat(exception.getModelId(), equalTo(REQUEST_ID));
    }

    @Test
    void testGetRequestWithRelations() {
        when(mockRepository.findByIdWithRelations(REQUEST_ID)).thenReturn(Optional.of(makeTestRequest()));

        final Request actual = service.getRequestWithRelations(REQUEST_ID, USER_ID);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findByIdWithRelations(REQUEST_ID);
        assertThat(actual, deepEqualTo(makeTestRequest()));
    }

    @Test
    void testGetRequestWithRelationsWhenUserNotFound() {
        when(userService.getUser(USER_ID)).thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getRequestWithRelations(REQUEST_ID, USER_ID));

        verify(userService).getUser(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testGetRequestWithRelationsWhenRequestNotFound() {
        when(mockRepository.findByIdWithRelations(REQUEST_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getRequestWithRelations(REQUEST_ID, USER_ID));

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findByIdWithRelations(REQUEST_ID);
        assertThat(exception.getModelName(), equalTo("request"));
        assertThat(exception.getModelId(), equalTo(REQUEST_ID));
    }

    @Test
    void testGetOwnRequests() {
        when(mockRepository.findAllByRequesterId(USER_ID, PAGE)).thenReturn(List.of(makeTestRequest()));

        final List<Request> actual = service.getOwnRequests(USER_ID, FROM, SIZE);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findAllByRequesterId(USER_ID, PAGE);
        assertThat(actual, contains(deepEqualTo(makeTestRequest())));
    }

    @Test
    void testGetOwnRequestsWhenUserNotFound() {
        when(userService.getUser(USER_ID)).thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getOwnRequests(USER_ID, FROM, SIZE));

        verify(userService).getUser(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testGetOthersRequests() {
        when(mockRepository.findAllOtherByRequesterId(USER_ID, PAGE)).thenReturn(List.of(makeTestRequest()));

        final List<Request> actual = service.getOthersRequests(USER_ID, FROM, SIZE);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findAllOtherByRequesterId(USER_ID, PAGE);
        assertThat(actual, contains(deepEqualTo(makeTestRequest())));
    }

    @Test
    void testGetOthersRequestsWhenUserNotFound() {
        when(userService.getUser(USER_ID)).thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getOthersRequests(USER_ID, FROM, SIZE));

        verify(userService).getUser(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }
}