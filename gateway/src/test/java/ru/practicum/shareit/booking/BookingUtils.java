package ru.practicum.shareit.booking;

import java.time.LocalDateTime;
import java.time.Month;

final class BookingUtils {

    private BookingUtils() {
    }

    static BookingCreateDto makeTestBookingCreateDto() {
        final BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(13L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        return dto;
    }
}
