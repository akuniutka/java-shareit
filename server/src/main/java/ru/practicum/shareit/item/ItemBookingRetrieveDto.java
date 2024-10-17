package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
class ItemBookingRetrieveDto {

    private Long id;
    private Long bookerId;
}
