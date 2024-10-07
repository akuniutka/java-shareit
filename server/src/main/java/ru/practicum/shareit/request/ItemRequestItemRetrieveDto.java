package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = "id")
class ItemRequestItemRetrieveDto {

    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}
