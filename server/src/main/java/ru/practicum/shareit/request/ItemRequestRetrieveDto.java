package ru.practicum.shareit.request;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
import java.util.Set;

@Data
@EqualsAndHashCode(of = "id")
class ItemRequestRetrieveDto {

    private Long id;
    private String description;
    private LocalDateTime created;
    private Set<ItemRequestItemRetrieveDto> items;
}
