package ru.practicum.shareit.item;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
interface ItemMapper {

    Item mapToItem(ItemCreateDto dto);

    Item mapToItem(ItemUpdateDto dto);

    ItemRetrieveDto mapToDto(Item item);

    List<ItemRetrieveDto> mapToDto(List<Item> items);
}
