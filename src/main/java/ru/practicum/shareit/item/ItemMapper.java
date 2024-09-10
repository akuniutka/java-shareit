package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.EntityCopier;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewItemDto;
import ru.practicum.shareit.item.dto.UpdateItemDto;

import java.util.List;

@Mapper
public interface ItemMapper extends EntityCopier<Item> {

    Item mapToItem(NewItemDto dto);

    Item mapToItem(UpdateItemDto dto);

    ItemDto mapToDto(Item item);

    List<ItemDto> mapToDto(List<Item> items);
}
