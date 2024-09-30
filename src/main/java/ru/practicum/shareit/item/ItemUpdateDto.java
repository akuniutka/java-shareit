package ru.practicum.shareit.item;

import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class ItemUpdateDto {

    @Size(max = 255)
    private String name;

    @Size(max = 2000)
    private String description;

    private Boolean available;
}
