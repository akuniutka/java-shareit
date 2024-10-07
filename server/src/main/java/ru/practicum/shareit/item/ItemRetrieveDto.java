package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
class ItemRetrieveDto {

    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
    private ItemBookingRetrieveDto lastBooking;
    private ItemBookingRetrieveDto nextBooking;
    private Set<CommentRetrieveDto> comments;
}
