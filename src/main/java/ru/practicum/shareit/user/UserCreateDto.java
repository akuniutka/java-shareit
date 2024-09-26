package ru.practicum.shareit.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
class UserCreateDto {

    @NotBlank
    private String name;

    @NotBlank
    private String email;
}
