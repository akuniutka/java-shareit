package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
class BookingRetrieveDto {

    private Long id;
    private BookingItemRetrieveDto item;
    private BookingBookerRetrieveDto booker;
    private LocalDateTime start;
    private LocalDateTime end;
    private String status;
}
