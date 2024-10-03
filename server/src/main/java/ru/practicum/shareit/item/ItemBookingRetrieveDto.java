package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
public class ItemBookingRetrieveDto {

    private Long id;
    private Long bookerId;
}
