package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingProxy;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBooking;
import static ru.practicum.shareit.common.CommonUtils.assertLogs;

class BookingServiceImplTest {

    private static final LogListener logListener = new LogListener(BookingServiceImpl.class);

    private static final long USER_ID = 42L;
    private static final long ITEM_ID = 13L;
    private static final long BOOKING_ID = 1L;
    private static final int FROM = 0;
    private static final int SIZE = 10;
    private static final Pageable PAGE = PageRequest.of(FROM / SIZE, SIZE, Sort.by(Sort.Direction.DESC, "start"));

    private AutoCloseable openMocks;

    @Mock
    private BookingRepository mockRepository;

    @Mock
    private UserService userService;

    @Mock
    private ItemService itemService;

    private InOrder inOrder;

    private BookingService service;

    @BeforeEach
    void setUp() {
        openMocks = MockitoAnnotations.openMocks(this);
        service = new BookingServiceImpl(mockRepository, userService, itemService);
        logListener.startListen();
        logListener.reset();
        inOrder = Mockito.inOrder(mockRepository, userService, itemService);
    }

    @AfterEach
    void tearDown() throws Exception {
        logListener.stopListen();
        Mockito.verifyNoMoreInteractions(mockRepository, userService, itemService);
        openMocks.close();
    }

    @Test
    void testCreateBooking() throws JSONException, IOException {
        final Booking bookingToSave = makeBookingProxy().withId(null).withItemName("The thing");
        final Item item = new Item();
        item.setName("The thing");
        item.setAvailable(true);
        when(itemService.getItemToBook(makeBookingProxy().getItem().getId(), makeBookingProxy().getBooker().getId()))
                .thenReturn(item);
        when(mockRepository.save(bookingToSave)).thenReturn(makeBookingProxy());

        final Booking actual = service.createBooking(makeBookingProxy().withId(null));

        inOrder.verify(userService).getUser(makeBookingProxy().getBooker().getId());
        inOrder.verify(itemService).getItemToBook(makeBookingProxy().getItem().getId(),
                makeBookingProxy().getBooker().getId());
        inOrder.verify(mockRepository).save(bookingToSave);
        assertThat(actual, equalTo(makeBookingProxy()));
        assertLogs(logListener.getEvents(), "create_booking.json", getClass());
    }

    @Test
    void testCreateBookingWhenWrongDates() {
        final Booking booking = makeBookingProxy().withId(null);
        booking.setStart(booking.getStart().plusMonths(2L));

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createBooking(booking));

        assertThat(exception.getProperty(), equalTo("end"));
        assertThat(exception.getViolation(), equalTo("should be after start"));
    }

    @Test
    void testCreateBookingWhenUserNotFound() {
        when(userService.getUser(makeBookingProxy().getBooker().getId()))
                .thenThrow(new NotFoundException(User.class, USER_ID));

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.createBooking(makeBookingProxy().withId(null)));

        inOrder.verify(userService).getUser(makeBookingProxy().getBooker().getId());
        assertThat(exception.getModelName(), equalTo("user"));
        assertThat(exception.getModelId(), equalTo(USER_ID));
    }

    @Test
    void testCreateBookingWhenItemUnavailable() {
        final Item item = new Item();
        item.setName("The thing");
        item.setAvailable(false);
        when(itemService.getItemToBook(makeBookingProxy().getItem().getId(), makeBookingProxy().getBooker().getId()))
                .thenReturn(item);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.createBooking(makeBookingProxy().withId(null)));

        inOrder.verify(userService).getUser(makeBookingProxy().getBooker().getId());
        inOrder.verify(itemService).getItemToBook(makeBookingProxy().getItem().getId(),
                makeBookingProxy().getBooker().getId());
        assertThat(exception.getProperty(), equalTo("item"));
        assertThat(exception.getViolation(), equalTo("unavailable item"));
    }

    @Test
    void testGetBooking() {
        when(mockRepository.findByIdAndBookerIdOrItemOwnerId(BOOKING_ID, USER_ID))
                .thenReturn(Optional.of(makeTestBooking()));

        final Booking actual = service.getBooking(BOOKING_ID, USER_ID);

        verify(mockRepository).findByIdAndBookerIdOrItemOwnerId(BOOKING_ID, USER_ID);
        assertThat(actual, samePropertyValuesAs(makeTestBooking()));
    }

    @Test
    void testGetBookingNotFound() {
        when(mockRepository.findByIdAndBookerIdOrItemOwnerId(BOOKING_ID, USER_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.getBooking(BOOKING_ID, USER_ID));

        verify(mockRepository).findByIdAndBookerIdOrItemOwnerId(BOOKING_ID, USER_ID);
        assertThat(exception.getModelName(), equalTo("booking"));
        assertThat(exception.getModelId(), equalTo(BOOKING_ID));
    }

    @Test
    void testGetUserBookingsWhenAll() {
        when(mockRepository.findAllByBookerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.ALL, FROM, SIZE);

        verify(mockRepository).findAllByBookerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenCurrent() {
        when(mockRepository.findCurrentByBookerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.CURRENT, FROM, SIZE);

        verify(mockRepository).findCurrentByBookerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenPast() {
        when(mockRepository.findPastByBookerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.PAST, FROM, SIZE);

        verify(mockRepository).findPastByBookerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenFuture() {
        when(mockRepository.findFutureByBookerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.FUTURE, FROM, SIZE);

        verify(mockRepository).findFutureByBookerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenWaiting() {
        when(mockRepository.findAllByBookerIdAndStatus(USER_ID, BookingStatus.WAITING, PAGE))
                .thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.WAITING, FROM, SIZE);

        verify(mockRepository).findAllByBookerIdAndStatus(USER_ID, BookingStatus.WAITING, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenRejected() {
        when(mockRepository.findAllByBookerIdAndStatus(USER_ID, BookingStatus.REJECTED, PAGE))
                .thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getUserBookings(USER_ID, BookingStatusFilter.REJECTED, FROM, SIZE);

        verify(mockRepository).findAllByBookerIdAndStatus(USER_ID, BookingStatus.REJECTED, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetUserBookingsWhenFilterNull() {
        assertThrows(AssertionError.class, () -> service.getUserBookings(USER_ID, null, FROM, SIZE));
    }

    @Test
    void testGetOwnerBookingsWhenAll() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findAllByItemOwnerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.ALL, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findAllByItemOwnerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenCurrent() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findCurrentByItemOwnerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.CURRENT, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findCurrentByItemOwnerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenPast() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findPastByItemOwnerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.PAST, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findPastByItemOwnerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenFuture() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findFutureByItemOwnerId(USER_ID, PAGE)).thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.FUTURE, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findFutureByItemOwnerId(USER_ID, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenWaiting() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findAllByItemOwnerIdAndStatus(USER_ID, BookingStatus.WAITING, PAGE))
                .thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.WAITING, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findAllByItemOwnerIdAndStatus(USER_ID, BookingStatus.WAITING, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenRejected() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);
        when(mockRepository.findAllByItemOwnerIdAndStatus(USER_ID, BookingStatus.REJECTED, PAGE))
                .thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.getOwnerBookings(USER_ID, BookingStatusFilter.REJECTED, FROM, SIZE);

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        inOrder.verify(mockRepository).findAllByItemOwnerIdAndStatus(USER_ID, BookingStatus.REJECTED, PAGE);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testGetOwnerBookingsWhenFilterNull() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(true);

        assertThrows(AssertionError.class, () -> service.getOwnerBookings(USER_ID, null, FROM, SIZE));

        verify(itemService).existByOwnerId(USER_ID);
    }

    @Test
    void testGetOwnerBookingsWhenNoItem() {
        when(itemService.existByOwnerId(USER_ID)).thenReturn(false);

        final ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> service.getOwnerBookings(USER_ID, BookingStatusFilter.REJECTED, FROM, SIZE));

        inOrder.verify(itemService).existByOwnerId(USER_ID);
        assertThat(exception.getMessage(), equalTo("You should owe items to get related bookings"));
    }

    @Test
    void testFindAllCompleteBookingByUserIdAndItemId() {
        when(mockRepository.findAllCompleteBookingByBookerIdAndItemId(USER_ID, ITEM_ID))
                .thenReturn(List.of(makeBookingProxy()));

        final List<Booking> actual = service.findAllCompleteBookingByUserIdAndItemId(USER_ID, ITEM_ID);

        verify(mockRepository).findAllCompleteBookingByBookerIdAndItemId(USER_ID, ITEM_ID);
        assertThat(actual, contains(makeBookingProxy()));
    }

    @Test
    void testApplyBookingVerdictWhenApprove() throws JSONException, IOException {
        final Booking booking = makeBookingProxy().withStatus(BookingStatus.WAITING);
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(USER_ID);
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userService.existsById(USER_ID)).thenReturn(true);
        when(mockRepository.save(makeBookingProxy())).thenReturn(makeBookingProxy());

        final Booking actual = service.applyBookingVerdict(BOOKING_ID, true, USER_ID);

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        inOrder.verify(userService).existsById(USER_ID);
        inOrder.verify(mockRepository).save(makeBookingProxy());
        assertThat(actual, equalTo(makeBookingProxy()));
        assertLogs(logListener.getEvents(), "approve_booking.json", getClass());
    }

    @Test
    void testApplyBookingVerdictWhenReject() throws JSONException, IOException {
        final Booking booking = makeBookingProxy().withStatus(BookingStatus.WAITING);
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(USER_ID);
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userService.existsById(USER_ID)).thenReturn(true);
        when(mockRepository.save(makeBookingProxy().withStatus(BookingStatus.REJECTED)))
                .thenReturn(makeBookingProxy().withStatus(BookingStatus.REJECTED));

        final Booking actual = service.applyBookingVerdict(BOOKING_ID, false, USER_ID);

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        inOrder.verify(userService).existsById(USER_ID);
        inOrder.verify(mockRepository).save(makeBookingProxy().withStatus(BookingStatus.REJECTED));
        assertThat(actual, equalTo(makeBookingProxy().withStatus(BookingStatus.REJECTED)));
        assertLogs(logListener.getEvents(), "reject_booking.json", getClass());
    }

    @Test
    void testApplyBookingVerdictWhenBookingNotFound() {
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.empty());

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.applyBookingVerdict(BOOKING_ID, false, USER_ID));

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        assertThat(exception.getModelName(), equalTo("booking"));
        assertThat(exception.getModelId(), equalTo(BOOKING_ID));
    }

    @Test
    void testApplyBookingVerdictUserNotExist() {
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.of(makeBookingProxy()));
        when(userService.existsById(USER_ID)).thenReturn(false);

        final ActionNotAllowedException exception = assertThrows(ActionNotAllowedException.class,
                () -> service.applyBookingVerdict(BOOKING_ID, false, USER_ID));

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        inOrder.verify(userService).existsById(USER_ID);
        assertThat(exception.getMessage(), equalTo("Not authorized"));
    }

    @Test
    void testApplyBookingVerdictWhenNotItemOwner() {
        final Booking booking = makeBookingProxy().withStatus(BookingStatus.WAITING);
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(USER_ID + 1);
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userService.existsById(USER_ID)).thenReturn(true);

        final NotFoundException exception = assertThrows(NotFoundException.class,
                () -> service.applyBookingVerdict(BOOKING_ID, false, USER_ID));

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        inOrder.verify(userService).existsById(USER_ID);
        assertThat(exception.getModelName(), equalTo("booking"));
        assertThat(exception.getModelId(), equalTo(BOOKING_ID));
    }

    @Test
    void testApplyBookingVerdictWhenWrongStatus() {
        final Booking booking = makeBookingProxy();
        booking.getItem().setOwner(new User());
        booking.getItem().getOwner().setId(USER_ID);
        when(mockRepository.findByIdWithBookerAndItemOwner(BOOKING_ID)).thenReturn(Optional.of(booking));
        when(userService.existsById(USER_ID)).thenReturn(true);

        final ValidationException exception = assertThrows(ValidationException.class,
                () -> service.applyBookingVerdict(BOOKING_ID, false, USER_ID));

        inOrder.verify(mockRepository).findByIdWithBookerAndItemOwner(BOOKING_ID);
        inOrder.verify(userService).existsById(USER_ID);
        assertThat(exception.getProperty(), equalTo("status"));
        assertThat(exception.getViolation(), equalTo("booking should be in status WAITING"));
    }
}