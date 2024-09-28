package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
class ItemRetrieveDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemBookingRetrieveDto lastBooking;
    private ItemBookingRetrieveDto nextBooking;
}
