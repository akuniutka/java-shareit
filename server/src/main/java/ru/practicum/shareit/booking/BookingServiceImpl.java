package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking createBooking(final Booking booking) {
        Objects.requireNonNull(booking, "Cannot create booking: is null");
        Objects.requireNonNull(booking.getItem(), "Cannot create booking: booking.item is null");
        Objects.requireNonNull(booking.getItem().getId(), "Cannot create booking: booking.item.id is null");
        Objects.requireNonNull(booking.getBooker(), "Cannot create booking: booking.booker is null");
        Objects.requireNonNull(booking.getBooker().getId(), "Cannot create booking: booking.booker.id is null");
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new ValidationException("end", "should be after start");
        }
        userService.getUser(booking.getBooker().getId());
        final Item item = itemService.getItemToBook(booking.getItem().getId(), booking.getBooker().getId());
        if (!item.getAvailable()) {
            throw new ValidationException("item", "unavailable item");
        }
        booking.getItem().setName(item.getName());
        final Booking createdBooking = repository.save(booking);
        log.info("Created booking with id = {}: {}", createdBooking.getId(), createdBooking);
        return createdBooking;
    }

    @Override
    public Booking getBooking(final long id, final long userId) {
        return repository.findByIdAndBookerIdOrItemOwnerId(id, userId).orElseThrow(
                () -> new NotFoundException(Booking.class, id)
        );
    }

    @Override
    public List<Booking> getUserBookings(final long userId, final BookingStatusFilter filter, final int from,
            final int size
    ) {
        final Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable page = PageRequest.of(from / size, size, sort);
        return switch (filter) {
            case ALL -> repository.findAllByBookerId(userId, page);
            case CURRENT -> repository.findCurrentByBookerId(userId, page);
            case PAST -> repository.findPastByBookerId(userId, page);
            case FUTURE -> repository.findFutureByBookerId(userId, page);
            case WAITING -> repository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, page);
            case REJECTED -> repository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, page);
            case null -> throw new AssertionError();
        };
    }

    @Override
    public List<Booking> getOwnerBookings(final long userId, final BookingStatusFilter filter, final int from,
            final int size
    ) {
        if (!itemService.existByOwnerId(userId)) {
            throw new ActionNotAllowedException("You should owe items to get related bookings");
        }
        final Sort sort = Sort.by(Sort.Direction.DESC, "start");
        final Pageable page = PageRequest.of(from / size, size, sort);
        return switch (filter) {
            case ALL -> repository.findAllByItemOwnerId(userId, page);
            case CURRENT -> repository.findCurrentByItemOwnerId(userId, page);
            case PAST -> repository.findPastByItemOwnerId(userId, page);
            case FUTURE -> repository.findFutureByItemOwnerId(userId, page);
            case WAITING -> repository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, page);
            case REJECTED -> repository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, page);
            case null -> throw new AssertionError();
        };
    }

    @Override
    public List<Booking> findAllCompleteBookingByUserIdAndItemId(long userId, long itemId) {
        return repository.findAllCompleteBookingByBookerIdAndItemId(userId, itemId);
    }

    @Override
    @Transactional
    public Booking applyBookingVerdict(final long id, final boolean isApproved, final long userId) {
        final Booking booking = repository.findByIdWithBookerAndItemOwner(id).orElseThrow(
                () -> new NotFoundException(Booking.class, id)
        );
        if (!userService.existsById(userId)) {
            throw new ActionNotAllowedException("Not authorized");
        }
        if (!Objects.equals(booking.getItem().getOwner().getId(), userId)) {
            throw new NotFoundException(Booking.class, id);
        }
        if (!BookingStatus.WAITING.equals(booking.getStatus())) {
            throw new ValidationException("status", "booking should be in status " + BookingStatus.WAITING);
        }
        booking.setStatus(isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED);
        final Booking updatedBooking = repository.save(booking);
        log.info("Changed status of booking id = {} to {}", id, updatedBooking.getStatus());
        return updatedBooking;
    }
}
