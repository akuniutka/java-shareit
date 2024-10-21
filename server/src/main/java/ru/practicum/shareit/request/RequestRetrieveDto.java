package ru.practicum.shareit.request;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.Set;

@Builder(toBuilder = true)
record RequestRetrieveDto(

        Long id,
        String description,
        LocalDateTime created,
        Set<ItemRetrieveDto> items) {

}
