package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.Request;

import java.util.List;

@Mapper(uses = CommentMapper.class)
interface ItemMapper {

    @Mapping(target = "owner.id", source = "userId")
    @Mapping(target = "comments", expression = "java(new java.util.HashSet<>())")
    @Mapping(target = "request", expression = "java(requestFromRequestId(dto))")
    Item mapToItem(Long userId, ItemCreateDto dto);

    Item mapToItem(ItemUpdateDto dto);

    @Mapping(target = "requestId", source = "request.id")
    ItemRetrieveDto mapToDto(Item item);

    List<ItemRetrieveDto> mapToDto(List<Item> items);

    @Mapping(target = "bookerId", source = "booker.id")
    ItemBookingRetrieveDto mapToDto(Booking booking);

    default Request requestFromRequestId(final ItemCreateDto dto) {
        if (dto == null || dto.getRequestId() == null) {
            return null;
        }
        final Request request = new Request();
        request.setId(dto.getRequestId());
        return request;
    }
}
