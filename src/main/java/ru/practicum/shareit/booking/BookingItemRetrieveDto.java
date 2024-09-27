package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
class BookingItemRetrieveDto {

    private Long id;
    private String name;
}
