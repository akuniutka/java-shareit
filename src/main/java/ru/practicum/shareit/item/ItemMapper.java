package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.Booking;

import java.util.List;

@Mapper(uses = CommentMapper.class)
interface ItemMapper {

    Item mapToItem(ItemCreateDto dto);

    Item mapToItem(ItemUpdateDto dto);

    ItemRetrieveDto mapToDto(Item item);

    List<ItemRetrieveDto> mapToDto(List<Item> items);

    @Mapping(source = "booker.id", target = "bookerId")
    ItemBookingRetrieveDto mapToDto(Booking booking);
}
