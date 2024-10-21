package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import ru.practicum.shareit.validation.NotBlankOrNull;

@Builder(toBuilder = true)
record UserUpdateDto(

        @NotBlankOrNull
        @Size(max = 255)
        String name,

        @NotBlankOrNull
        @Email
        @Size(max = 255)
        String email) {

}
