package ru.practicum.shareit.item;

import lombok.Data;

@Data
class ItemUpdateDto {

    private String name;
    private String description;
    private Boolean available;
}
