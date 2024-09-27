package ru.practicum.shareit.booking;

import java.util.List;

public interface BookingService {

    Booking createBooking(Booking booking, long userId);

    Booking getBooking(long id, long userId);

    List<Booking> getUserBookings(long userId, BookingStatusFilter filter);

    List<Booking> getOwnerBookings(long userId, BookingStatusFilter filter);

    Booking applyBookingVerdict(long id, boolean isApproved, long userId);
}
