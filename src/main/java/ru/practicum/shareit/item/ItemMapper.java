package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.EntityCopier;

import java.util.List;

@Mapper
interface ItemMapper extends EntityCopier<Item> {

    Item mapToItem(ItemCreateDto dto);

    Item mapToItem(ItemUpdateDto dto);

    ItemRetrieveDto mapToDto(Item item);

    List<ItemRetrieveDto> mapToDto(List<Item> items);
}
