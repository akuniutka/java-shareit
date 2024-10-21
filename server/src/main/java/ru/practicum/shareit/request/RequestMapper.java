package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.Item;

import java.util.List;

@Mapper
interface RequestMapper {

    @Mapping(target = "requester.id", source = "userId")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "items", expression = "java(new java.util.HashSet<>())")
    Request mapToRequest(Long userId, RequestCreateDto dto);

    RequestRetrieveDto mapToDto(Request request);

    List<RequestRetrieveDto> mapToDto(List<Request> requests);

    @Mapping(target = "ownerId", source = "owner.id")
    @Mapping(target = "requestId", source = "request.id")
    ItemRetrieveDto mapToDto(Item item);
}
