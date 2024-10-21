package ru.practicum.shareit.request;

import lombok.Builder;

@Builder(toBuilder = true)
record ItemRetrieveDto(

        Long id,
        Long ownerId,
        String name,
        String description,
        Boolean available,
        Long requestId) {

}
