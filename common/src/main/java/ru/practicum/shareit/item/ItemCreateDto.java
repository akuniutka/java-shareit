package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class ItemCreateDto {

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;
}
