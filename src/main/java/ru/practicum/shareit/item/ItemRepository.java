package ru.practicum.shareit.item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item save(Item item);

    Optional<Item> findById(long id);

    List<Item> findByOwnerId(long ownerId);

    List<Item> findByNameOrDescription(String text);

    Item update(Item item);

    boolean delete(long id);
}
