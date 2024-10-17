package ru.practicum.shareit.item;

import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.validation.NotBlankOrNull;

@Data
class ItemUpdateDto {

    @NotBlankOrNull
    @Size(max = 255)
    private String name;

    @NotBlankOrNull
    @Size(max = 2000)
    private String description;

    private Boolean available;
}
