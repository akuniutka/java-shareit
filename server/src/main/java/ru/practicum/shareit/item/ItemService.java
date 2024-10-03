package ru.practicum.shareit.item;

import java.util.List;

public interface ItemService {

    Item createItem(Item item, long userId);

    Item getItem(long id, long userId);

    Item getItemToBook(long id, long userId);

    List<Item> getItems(long userId);

    List<Item> getItems(String text, long userId);

    boolean existByOwnerId(long userId);

    Item updateItem(long id, Item item, long userId);

    void deleteItem(long id, long userId);
}
