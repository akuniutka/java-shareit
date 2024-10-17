package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Mapper
interface ItemRequestMapper {

    @Mapping(target = "requester.id", source = "userId")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "items", expression = "java(new java.util.HashSet<>())")
    ItemRequest mapToItemRequest(Long userId, ItemRequestCreateDto dto);

    ItemRequestRetrieveDto mapToDto(ItemRequest itemRequest);

    List<ItemRequestRetrieveDto> mapToDto(List<ItemRequest> itemRequests);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemRequestItemRetrieveDto mapToDto(Item item);
}
