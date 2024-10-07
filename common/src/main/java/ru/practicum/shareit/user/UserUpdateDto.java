package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class UserUpdateDto {

    @Size(max = 255)
    private String name;

    @Email
    @Size(max = 255)
    private String email;
}
