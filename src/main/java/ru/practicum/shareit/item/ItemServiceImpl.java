package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class ItemServiceImpl implements ItemService {

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
        userService.getUser(userId);
        return repository.findById(id)
                .orElseThrow(
                        () -> new NotFoundException(Item.class, id)
                );
    }

    @Override
    public List<Item> getItems(final long userId) {
        userService.getUser(userId);
        return repository.findByOwnerId(userId);
    }

    @Override
    public List<Item> getItems(final String text, final long userId) {
        userService.getUser(userId);
        return "".equals(text) ? List.of() : repository.findByNameOrDescription(text);
    }

    @Override
    @Transactional
    public Item updateItem(final long id, final Item update, final long userId) {
        Objects.requireNonNull(update, "Cannot update item: is null");
        final Item item = repository.findByIdWithOwner(id).orElseThrow(
                () -> new NotFoundException(Item.class, id)
        );
        final User user = userService.getUser(userId);
        if (!Objects.equals(item.getOwner(), user)) {
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
        repository.findByIdWithOwner(id)
                .filter(item -> !Objects.equals(item.getOwner().getId(), userId))
                .ifPresent(item -> {
                    throw new ActionNotAllowedException("Only owner can delete item");
                });
        if (repository.delete(id) != 0) {
            log.info("Deleted item with id = {}", id);
        } else {
            log.info("No item deleted: item with id = {} doe not exist", id);
        }
    }
}
