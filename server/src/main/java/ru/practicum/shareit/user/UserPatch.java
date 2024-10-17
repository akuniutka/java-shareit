package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import ru.practicum.shareit.validation.NotBlankOrNull;

@Data
public class UserPatch {

    @NotNull
    private Long userId;

    @NotBlankOrNull
    @Size(max = 255)
    private String name;

    @NotBlankOrNull
    @Email
    @Size(max = 255)
    private String email;
}
