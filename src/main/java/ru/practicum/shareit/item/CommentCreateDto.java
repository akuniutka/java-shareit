package ru.practicum.shareit.item;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class CommentCreateDto {

    @NotBlank
    @Size(max = 2000)
    private String text;
}
