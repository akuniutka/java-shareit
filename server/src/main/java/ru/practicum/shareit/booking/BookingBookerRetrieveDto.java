package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
class BookingBookerRetrieveDto {

    private Long id;
}
