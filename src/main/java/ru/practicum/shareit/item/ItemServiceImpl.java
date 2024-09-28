package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;

    @Override
    @Transactional
    public Item createItem(final Item item, final long userId) {
        Objects.requireNonNull(item, "Cannot create item: is null");
        final User user = userService.getUser(userId);
        item.setOwner(user);
        final Item createdItem = repository.save(item);
        log.info("Created item with id = {}: {}", createdItem.getId(), createdItem);
        return createdItem;
    }

    @Override
    public Item getItem(final long id, final long userId) {
        return repository.findById(id)
                .map(item -> maskDataByUserRights(item, userId))
                .orElseThrow(() -> new NotFoundException(Item.class, id));
    }

    @Override
    public List<Item> getItems(final long userId) {
        return repository.findByOwnerId(userId, Sort.by("id")).stream()
                .map(item -> maskDataByUserRights(item, userId))
                .toList();
    }

    @Override
    public List<Item> getItems(final String text, final long userId) {
        return "".equals(text) ? List.of() : repository.findByNameOrDescription(text, Sort.by("id")).stream()
                .map(item -> maskDataByUserRights(item, userId))
                .toList();
    }

    @Override
    public boolean existByOwnerId(long userId) {
        return repository.existsByOwnerId(userId);
    }

    @Override
    @Transactional
    public Item updateItem(final long id, final Item update, final long userId) {
        Objects.requireNonNull(update, "Cannot update item: is null");
        final Item item = repository.findById(id).orElseThrow(
                () -> new NotFoundException(Item.class, id)
        );
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            throw new ActionNotAllowedException("Only owner can update item");
        }
        Optional.ofNullable(update.getName()).ifPresent(item::setName);
        Optional.ofNullable(update.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(update.getAvailable()).ifPresent(item::setAvailable);
        final Item updatedItem = repository.save(item);
        log.info("Updated item with id = {}: {}", id, updatedItem);
        return updatedItem;
    }

    @Override
    @Transactional
    public void deleteItem(final long id, final long userId) {
        repository.findById(id)
                .filter(item -> !Objects.equals(item.getOwner().getId(), userId))
                .ifPresent(item -> {
                    throw new ActionNotAllowedException("Only owner can delete item");
                });
        if (repository.delete(id) != 0) {
            log.info("Deleted item with id = {}", id);
        } else {
            log.info("No item deleted: item with id = {} does not exist", id);
        }
    }

    private Item maskDataByUserRights(final Item item, final long userId) {
        if (!Objects.equals(item.getOwner().getId(), userId)) {
            item.setLastBooking(null);
            item.setNextBooking(null);
        }
        return item;
    }
}
