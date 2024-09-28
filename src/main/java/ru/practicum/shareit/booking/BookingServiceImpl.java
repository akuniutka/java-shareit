package ru.practicum.shareit.booking;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.common.exception.ValidationException;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
@Slf4j
class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserService userService;
    private final ItemService itemService;

    @Override
    @Transactional
    public Booking createBooking(final Booking booking, final long userId) {
        Objects.requireNonNull(booking, "Cannot create booking: is null");
        final User user = userService.getUser(userId);
        final LocalDateTime now = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
        if (booking.getStart().isBefore(now)) {
            throw new ValidationException("start", "cannot be in past");
        }
        if (!booking.getEnd().isAfter(booking.getStart())) {
            throw new ValidationException("end", "should be after start");
        }
        if (booking.getItem() == null) {
            throw new ValidationException("item", "cannot be null");
        }
        if (booking.getItem().getId() == null) {
            throw new ValidationException("item.id", "cannot be null");
        }
        final Item item = itemService.getItem(booking.getItem().getId(), userId);
        if (Objects.equals(item.getOwner().getId(), userId)) {
            throw new NotFoundException(Item.class, booking.getItem().getId());
        }
        if (!item.getAvailable()) {
            throw new ValidationException("item", "unavailable item");
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
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
    public List<Booking> getUserBookings(final long userId, final BookingStatusFilter filter) {
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (filter) {
            case ALL -> repository.findAllByBookerId(userId, sort);
            case CURRENT -> repository.findCurrentByBookerId(userId, sort);
            case PAST -> repository.findPastByBookerId(userId, sort);
            case FUTURE -> repository.findFutureByBookerId(userId, sort);
            case WAITING -> repository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> repository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case null -> throw new AssertionError();
        };
    }

    @Override
    public List<Booking> getOwnerBookings(final long userId, final BookingStatusFilter filter) {
        if (!itemService.existByOwnerId(userId)) {
            throw new ActionNotAllowedException("You should owe items to get related bookings");
        }
        Sort sort = Sort.by(Sort.Direction.DESC, "start");
        return switch (filter) {
            case ALL -> repository.findAllByItemOwnerId(userId, sort);
            case CURRENT -> repository.findCurrentByItemOwnerId(userId, sort);
            case PAST -> repository.findPastByItemOwnerId(userId, sort);
            case FUTURE -> repository.findFutureByItemOwnerId(userId, sort);
            case WAITING -> repository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, sort);
            case REJECTED -> repository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, sort);
            case null -> throw new AssertionError();
        };
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
