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
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestProxy;

class ItemRequestServiceImplTest {

    private static final LogListener logListener = new LogListener(ItemRequestServiceImpl.class);

    private static final long USER_ID = 42L;
    private static final long REQUEST_ID = 7L;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final Pageable PAGE = PageRequest.of(FROM / SIZE, SIZE, Sort.by(Sort.Direction.DESC, "created"));

    private AutoCloseable openMocks;

    @Mock
    private ItemRequestRepository mockRepository;

    @Mock
    private UserService userService;

    private InOrder inOrder;

    private ItemRequestService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new ItemRequestServiceImpl(mockRepository, userService);
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
    void testCreateItemRequest() throws JSONException, IOException {
        when(mockRepository.save(makeItemRequestProxy())).thenReturn(makeItemRequestProxy());

        final ItemRequest actual = service.createItemRequest(makeItemRequestProxy());

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).save(makeItemRequestProxy());
        assertThat(actual, equalTo(makeItemRequestProxy()));
        assertLogs(logListener.getEvents(), "create_item_request.json", getClass());
    }

    @Test
    void testGetItemRequest() {
        when(mockRepository.findById(REQUEST_ID)).thenReturn(Optional.of(makeItemRequestProxy()));

        final ItemRequest actual = service.getItemRequest(REQUEST_ID);

        verify(mockRepository).findById(REQUEST_ID);
        assertThat(actual, equalTo(makeItemRequestProxy()));
    }

    @Test
    void testGetItemRequestWhenNotFound() {
        when(mockRepository.findById(REQUEST_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemRequest(REQUEST_ID));

        verify(mockRepository).findById(REQUEST_ID);
        assertThat(exception.getModelName(), equalTo("itemrequest"));
        assertThat(exception.getModelId(), equalTo(REQUEST_ID));
    }

    @Test
    void testGetItemRequestWithRelations() {
        when(mockRepository.findByIdWithRelations(REQUEST_ID)).thenReturn(Optional.of(makeItemRequestProxy()));

        final ItemRequest actual = service.getItemRequestWithRelations(REQUEST_ID, USER_ID);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findByIdWithRelations(REQUEST_ID);
        assertThat(actual, equalTo(makeItemRequestProxy()));
    }

    @Test
    void testGetItemRequestWithRelationsWhenUserNotFound() {
        when(userService.getUser(USER_ID)).thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemRequestWithRelations(REQUEST_ID, USER_ID));

        verify(userService).getUser(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testGetItemRequestWithRelationsWhenRequestNotFound() {
        when(mockRepository.findByIdWithRelations(REQUEST_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemRequestWithRelations(REQUEST_ID, USER_ID));

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findByIdWithRelations(REQUEST_ID);
        assertThat(exception.getModelName(), equalTo("itemrequest"));
        assertThat(exception.getModelId(), equalTo(REQUEST_ID));
    }

    @Test
    void testGetOwnRequests() {
        when(mockRepository.findAllByRequesterId(USER_ID, PAGE)).thenReturn(List.of(makeItemRequestProxy()));

        final List<ItemRequest> actual = service.getOwnRequests(USER_ID, FROM, SIZE);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findAllByRequesterId(USER_ID, PAGE);
        assertThat(actual, contains(makeItemRequestProxy()));
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
        when(mockRepository.findAllOtherByRequesterId(USER_ID, PAGE)).thenReturn(List.of(makeItemRequestProxy()));

        final List<ItemRequest> actual = service.getOthersRequests(USER_ID, FROM, SIZE);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).findAllOtherByRequesterId(USER_ID, PAGE);
        assertThat(actual, contains(makeItemRequestProxy()));
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