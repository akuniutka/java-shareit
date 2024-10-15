package ru.practicum.shareit.booking;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;

final class BookingUtils {

    private BookingUtils() {
    }

    static Booking makeBookingProxy() {
        final Booking booking = new BookingProxy();
        booking.setId(1L);
        booking.setItem(new Item());
        booking.getItem().setId(13L);
        booking.setBooker(new User());
        booking.getBooker().setId(42L);
        booking.setStart(LocalDateTime.of(2001, Month.JUNE, 1, 9, 10, 11));
        booking.setEnd(LocalDateTime.of(2001, Month.JUNE, 30, 10, 11, 12));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    static BookingCreateDto makeBookingCreateDtoProxy() {
        final BookingCreateDto dto = new BookingCreateDtoProxy();
        dto.setItemId(13L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        return dto;
    }

    static BookingRetrieveDto makeBookingRetrieveDtoProxy() {
        final BookingRetrieveDto dto = new BookingRetrieveDtoProxy();
        dto.setId(1L);
        dto.setItem(new BookingItemRetrieveDto());
        dto.getItem().setId(13L);
        dto.setBooker(new BookingBookerRetrieveDto());
        dto.getBooker().setId(42L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        dto.setStatus("WAITING");
        return dto;
    }
}
