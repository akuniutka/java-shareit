package ru.practicum.shareit.user;

import lombok.Builder;

@Builder(toBuilder = true)
record UserRetrieveDto(

        Long id,
        String name,
        String email) {

}
