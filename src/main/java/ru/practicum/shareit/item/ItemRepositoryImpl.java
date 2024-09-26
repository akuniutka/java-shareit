package ru.practicum.shareit.item;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseRepository;

import java.util.List;
import java.util.Objects;

@Repository
class ItemRepositoryImpl extends BaseRepository<Item> implements ItemRepository {

    public ItemRepositoryImpl(final ItemMapper mapper) {
        super(Item::getId, Item::setId, mapper);
    }

    @Override
    public List<Item> findByOwnerId(final long ownerId) {
        return findAll().stream()
                .filter(item -> Objects.equals(item.getOwnerId(), ownerId))
                .toList();
    }

    @Override
    public List<Item> findByNameOrDescription(final String text) {
        return findAll().stream()
                .filter(item -> containsIgnoreCase(item.getName(), text)
                        || containsIgnoreCase(item.getDescription(), text))
                .filter(Item::getAvailable)
                .toList();
    }

    private boolean containsIgnoreCase(final String string, final String text) {
        return string.toLowerCase().contains(text.toLowerCase());
    }
}
