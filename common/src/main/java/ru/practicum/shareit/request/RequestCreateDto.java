package ru.practicum.shareit.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

record RequestCreateDto(

        @NotBlank
        @Size(max = 2000)
        String description) {

}
