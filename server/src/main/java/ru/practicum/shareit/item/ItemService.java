package ru.practicum.shareit.item;

import jakarta.validation.Valid;

import java.util.List;

public interface ItemService {

    Item createItem(@Valid Item item, long userId);

    Item getItem(long id, long userId);

    Item getItemToBook(long id, long userId);

    List<Item> getItems(long userId);

    List<Item> getItems(String text, long userId);

    boolean existByOwnerId(long userId);

    Item updateItem(long id, @Valid Item item, long userId);

    void deleteItem(long id, long userId);
}
