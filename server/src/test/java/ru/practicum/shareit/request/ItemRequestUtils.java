package ru.practicum.shareit.request;

import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;

final class ItemRequestUtils {

    private ItemRequestUtils() {
    }

    static ItemRequest makeItemRequestProxy() {
        final ItemRequest itemRequest = new ItemRequestProxy();
        itemRequest.setId(7L);
        itemRequest.setRequester(new User());
        itemRequest.getRequester().setId(42L);
        itemRequest.setDescription("Need the thing");
        itemRequest.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        itemRequest.setItems(new HashSet<>());
        return itemRequest;
    }

    static ItemRequestCreateDto makeItemRequestCreateDtoProxy() {
        final ItemRequestCreateDto dto = new ItemRequestCreateDtoProxy();
        dto.setDescription("Need the thing");
        return dto;
    }

    static ItemRequestRetrieveDto makeItemRequestRetrieveDtoProxy() {
        final ItemRequestRetrieveDto dto = new ItemRequestRetrieveDtoProxy();
        dto.setId(7L);
        dto.setDescription("Need the thing");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        dto.setItems(new HashSet<>());
        return dto;
    }
}
