package ru.practicum.shareit.item;

import org.json.JSONException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.common.LogListener;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.request.RequestService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.common.CommonUtils.ANOTHER_USER_ID;
import static ru.practicum.shareit.common.CommonUtils.ITEM_ID;
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;
import static ru.practicum.shareit.item.ItemUtils.makeItemProxy;
import static ru.practicum.shareit.item.ItemUtils.makeNewItemProxy;

class ItemServiceImplTest {

    private static final LogListener logListener = new LogListener(ItemServiceImpl.class);

    private static final Sort SORT = Sort.by("id");
    private static final String SEARCH_TEXT = "text";

    private AutoCloseable openMocks;

    @Mock
    private ItemRepository mockRepository;

    @Mock
    private UserService userService;

    @Mock
    private RequestService requestService;

    private InOrder inOrder;

    private ItemService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new ItemServiceImpl(mockRepository, userService, requestService);
        logListener.startListen();
        logListener.reset();
        inOrder = Mockito.inOrder(mockRepository, userService, requestService);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, userService, requestService);
        openMocks.close();
    }

    @Test
    void testCreateItem() throws JSONException, IOException {
        when(mockRepository.save(makeNewItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.createItem(makeNewItemProxy());

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(mockRepository).save(makeNewItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "create_item.json", getClass());
    }

    @Test
    void testCreateItemWithRequest() throws JSONException, IOException {
        final Item expected = makeItemProxy();
        expected.setRequest(new Request());
        expected.getRequest().setId(REQUEST_ID);
        final Item itemReturned = makeItemProxy();
        itemReturned.setRequest(new Request());
        itemReturned.getRequest().setId(REQUEST_ID);
        final Item item = makeNewItemProxy();
        item.setRequest(new Request());
        item.getRequest().setId(REQUEST_ID);
        final Item newItem = makeNewItemProxy();
        newItem.setRequest(new Request());
        newItem.getRequest().setId(REQUEST_ID);
        when(mockRepository.save(item)).thenReturn(itemReturned);

        final Item actual = service.createItem(newItem);

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(requestService).getRequest(REQUEST_ID);
        inOrder.verify(mockRepository).save(item);
        assertThat(actual, equalTo(expected));
        assertLogs(logListener.getEvents(), "create_item_with_request.json", getClass());
    }

    @Test
    void testCreateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class, () -> service.createItem(null));

        assertThat(exception.getMessage(), equalTo("Cannot create item: is null"));
    }

    @Test
    void testCreateItemWhenUserNotFound() {
        when(userService.getUser(USER_ID)).thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.createItem(makeNewItemProxy()));

        verify(userService).getUser(USER_ID);
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testCreateItemWhenRequestNotFound() {
        final Item newItem = makeNewItemProxy();
        newItem.setRequest(new Request());
        newItem.getRequest().setId(REQUEST_ID);
        when(requestService.getRequest(REQUEST_ID)).thenThrow(new NotFoundException(Request.class, REQUEST_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class, () -> service.createItem(newItem));

        inOrder.verify(userService).getUser(USER_ID);
        inOrder.verify(requestService).getRequest(REQUEST_ID);
        assertThat(exception.getModelName(), equalTo("request"));
        assertThat(exception.getModelId(), equalTo(REQUEST_ID));
    }

    @Test
    void testGetItemWhenOwner() {
        final Item expected = makeItemProxy();
        expected.setLastBooking(new Booking());
        expected.getLastBooking().setId(1L);
        expected.setNextBooking(new Booking());
        expected.getNextBooking().setId(2L);
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByIdWithRelations(ITEM_ID)).thenReturn(Optional.of(returnedBooking));

        final Item actual = service.getItem(ITEM_ID, USER_ID);

        verify(mockRepository).findByIdWithRelations(ITEM_ID);
        assertThat(actual, equalTo(expected));
    }

    @Test
    void testGetItemWhenNotOwner() {
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByIdWithRelations(ITEM_ID)).thenReturn(Optional.of(returnedBooking));

        final Item actual = service.getItem(ITEM_ID, ANOTHER_USER_ID);

        verify(mockRepository).findByIdWithRelations(ITEM_ID);
        assertThat(actual, equalTo(makeItemProxy()));
    }

    @Test
    void testGetItemWhenNotFound() {
        when(mockRepository.findByIdWithRelations(ITEM_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItem(ITEM_ID, USER_ID));

        verify(mockRepository).findByIdWithRelations(ITEM_ID);
        assertThat(exception.getModelName(), equalTo("item"));
        assertThat(exception.getModelId(), equalTo(ITEM_ID));
    }

    @Test
    void testGetItemToBookWhenNotOwner() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));

        final Item actual = service.getItemToBook(ITEM_ID, ANOTHER_USER_ID);

        verify(mockRepository).findById(ITEM_ID);
        assertThat(actual, equalTo(makeItemProxy()));
    }

    @Test
    void testGetItemToBookWhenOwner() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemToBook(ITEM_ID, USER_ID));

        verify(mockRepository).findById(ITEM_ID);
        assertThat(exception.getModelName(), equalTo("item"));
        assertThat(exception.getModelId(), equalTo(ITEM_ID));
    }

    @Test
    void testGetItemToBookWhenNotFound() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getItemToBook(ITEM_ID, USER_ID));

        verify(mockRepository).findById(ITEM_ID);
        assertThat(exception.getModelName(), equalTo("item"));
        assertThat(exception.getModelId(), equalTo(ITEM_ID));
    }

    @Test
    void testGetItemsWhenOwner() {
        final Item expected = makeItemProxy();
        expected.setLastBooking(new Booking());
        expected.getLastBooking().setId(1L);
        expected.setNextBooking(new Booking());
        expected.getNextBooking().setId(2L);
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByOwnerId(USER_ID, SORT)).thenReturn(List.of(returnedBooking));

        final List<Item> actual = service.getItems(USER_ID);

        verify(mockRepository).findByOwnerId(USER_ID, SORT);
        assertThat(actual, contains(expected));
    }

    @Test
    void testGetItemsWhenNotOwner() {
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByOwnerId(ANOTHER_USER_ID, SORT)).thenReturn(List.of(returnedBooking));

        final List<Item> actual = service.getItems(ANOTHER_USER_ID);

        verify(mockRepository).findByOwnerId(ANOTHER_USER_ID, SORT);
        assertThat(actual, contains(makeItemProxy()));
    }

    @Test
    void testGetItemsWithTextWhenOwner() {
        final Item expected = makeItemProxy();
        expected.setLastBooking(new Booking());
        expected.getLastBooking().setId(1L);
        expected.setNextBooking(new Booking());
        expected.getNextBooking().setId(2L);
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByNameOrDescription(SEARCH_TEXT, SORT)).thenReturn(List.of(returnedBooking));

        final List<Item> actual = service.getItems(SEARCH_TEXT, USER_ID);

        verify(mockRepository).findByNameOrDescription(SEARCH_TEXT, SORT);
        assertThat(actual, contains(expected));
    }

    @Test
    void testGetItemsWithTextWhenNotOwner() {
        final Item returnedBooking = makeItemProxy();
        returnedBooking.setLastBooking(new Booking());
        returnedBooking.getLastBooking().setId(1L);
        returnedBooking.setNextBooking(new Booking());
        returnedBooking.getNextBooking().setId(2L);
        when(mockRepository.findByNameOrDescription(SEARCH_TEXT, SORT)).thenReturn(List.of(returnedBooking));

        final List<Item> actual = service.getItems(SEARCH_TEXT, ANOTHER_USER_ID);

        verify(mockRepository).findByNameOrDescription(SEARCH_TEXT, SORT);
        assertThat(actual, contains(makeItemProxy()));
    }

    @Test
    void testGetItemsWithTextWhenBlank() {
        final List<Item> actual = service.getItems("", USER_ID);

        assertThat(actual, empty());
    }

    @Test
    void testExistByOwnerIdWhenExist() {
        when(mockRepository.existsByOwnerId(USER_ID)).thenReturn(true);

        final boolean actual = service.existByOwnerId(USER_ID);

        verify(mockRepository).existsByOwnerId(USER_ID);
        assertThat(actual, equalTo(true));
    }

    @Test
    void testExistByOwnerIdWhenNotExist() {
        when(mockRepository.existsByOwnerId(USER_ID)).thenReturn(false);

        final boolean actual = service.existByOwnerId(USER_ID);

        verify(mockRepository).existsByOwnerId(USER_ID);
        assertThat(actual, equalTo(false));
    }

    @Test
    void testUpdateItem() throws JSONException, IOException {
        final Item returnedItem = makeItemProxy();
        returnedItem.setName(null);
        returnedItem.setDescription(null);
        returnedItem.setAvailable(null);
        final Item update = new ItemProxy();
        update.setName(makeItemProxy().getName());
        update.setDescription(makeItemProxy().getDescription());
        update.setAvailable(makeItemProxy().getAvailable());
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(returnedItem));
        when(mockRepository.save(makeItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.updateItem(ITEM_ID, update, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).save(makeItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "update_item_all_fields.json", getClass());
    }

    @Test
    void testUpdateItemWhenOnlyName() throws JSONException, IOException {
        final Item returnedItem = makeItemProxy();
        returnedItem.setName(null);
        final Item update = new ItemProxy();
        update.setName(makeItemProxy().getName());
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(returnedItem));
        when(mockRepository.save(makeItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.updateItem(ITEM_ID, update, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).save(makeItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "update_item_name.json", getClass());
    }

    @Test
    void testUpdateItemWhenOnlyDescription() throws JSONException, IOException {
        final Item returnedItem = makeItemProxy();
        returnedItem.setDescription(null);
        final Item update = new ItemProxy();
        update.setDescription(makeItemProxy().getDescription());
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(returnedItem));
        when(mockRepository.save(makeItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.updateItem(ITEM_ID, update, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).save(makeItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "update_item_description.json", getClass());
    }

    @Test
    void testUpdateItemWhenOnlyAvailable() throws JSONException, IOException {
        final Item returnedItem = makeItemProxy();
        returnedItem.setAvailable(null);
        final Item update = new ItemProxy();
        update.setAvailable(makeItemProxy().getAvailable());
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(returnedItem));
        when(mockRepository.save(makeItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.updateItem(ITEM_ID, update, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).save(makeItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "update_item_available.json", getClass());
    }

    @Test
    void testUpdateItemWhenNothing() throws JSONException, IOException {
        final Item update = new ItemProxy();
        update.setName(null);
        update.setDescription(null);
        update.setAvailable(null);
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));
        when(mockRepository.save(makeItemProxy())).thenReturn(makeItemProxy());

        final Item actual = service.updateItem(ITEM_ID, update, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).save(makeItemProxy());
        assertThat(actual, equalTo(makeItemProxy()));
        assertLogs(logListener.getEvents(), "update_item_all_fields.json", getClass());
    }

    @Test
    void testUpdateItemWhenNull() {
        final NullPointerException exception = assertThrows(NullPointerException.class,
                () -> service.updateItem(ITEM_ID, null, USER_ID));

        assertThat(exception.getMessage(), equalTo("Cannot update item: is null"));
    }

    @Test
    void testUpdateItemWhenNotFound() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.updateItem(ITEM_ID, makeItemProxy(), USER_ID));

        verify(mockRepository).findById(ITEM_ID);
        assertThat(exception.getModelName(), equalTo("item"));
        assertThat(exception.getModelId(), equalTo(ITEM_ID));
    }

    @Test
    void testUpdateItemWhenNotOwner() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));

        final ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> service.updateItem(ITEM_ID, makeItemProxy(), ANOTHER_USER_ID));

        verify(mockRepository).findById(ITEM_ID);
        assertThat(exception.getMessage(), equalTo("Only owner can update item"));
    }

    @Test
    void testDeleteItem() throws JSONException, IOException {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));
        when(mockRepository.delete(ITEM_ID)).thenReturn(1);

        service.deleteItem(ITEM_ID, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).delete(ITEM_ID);
        assertLogs(logListener.getEvents(), "delete_item.json", getClass());
    }

    @Test
    void testDeleteItemWhenNotFound() throws JSONException, IOException {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.empty());
        when(mockRepository.delete(ITEM_ID)).thenReturn(0);

        service.deleteItem(ITEM_ID, USER_ID);

        inOrder.verify(mockRepository).findById(ITEM_ID);
        inOrder.verify(mockRepository).delete(ITEM_ID);
        assertLogs(logListener.getEvents(), "delete_item_not_found.json", getClass());
    }

    @Test
    void testDeleteItemWhenNotOwner() {
        when(mockRepository.findById(ITEM_ID)).thenReturn(Optional.of(makeItemProxy()));

        final ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> service.deleteItem(ITEM_ID, ANOTHER_USER_ID));

        verify(mockRepository).findById(ITEM_ID);
        assertThat(exception.getMessage(), equalTo("Only owner can delete item"));
    }
}