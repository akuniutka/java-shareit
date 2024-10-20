package ru.practicum.shareit.booking;

import jakarta.validation.Valid;

import java.util.List;

public interface BookingService {

    Booking createBooking(@Valid Booking booking);

    Booking getBooking(long id, long userId);

    List<Booking> getUserBookings(long userId, BookingStatusFilter filter, int from, int size);

    List<Booking> getOwnerBookings(long userId, BookingStatusFilter filter, int from, int size);

    List<Booking> findAllCompleteBookingByUserIdAndItemId(long userId, long itemId);

    Booking applyBookingVerdict(long id, boolean isApproved, long userId);
}
