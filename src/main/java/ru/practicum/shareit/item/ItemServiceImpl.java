package ru.practicum.shareit.item;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.ActionNotAllowedException;
import ru.practicum.shareit.common.exception.NotFoundException;
import ru.practicum.shareit.user.UserService;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final UserService userService;
    private final Validator validator;

    @Override
    public Item createItem(final Item item, final long userId) {
        Objects.requireNonNull(item, "Cannot create item: is null");
        userService.getUser(userId);
        item.setOwnerId(userId);
        validate(item);
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
    public Item updateItem(final long id, final Item update, final long userId) {
        Objects.requireNonNull(update, "Cannot update item: is null");
        final Item item = repository.findById(id).orElseThrow(
                () -> new NotFoundException(Item.class, id)
        );
        userService.getUser(userId);
        if (!Objects.equals(item.getOwnerId(), userId)) {
            throw new ActionNotAllowedException("Only owner can update item");
        }
        Optional.ofNullable(update.getName()).ifPresent(item::setName);
        Optional.ofNullable(update.getDescription()).ifPresent(item::setDescription);
        Optional.ofNullable(update.getAvailable()).ifPresent(item::setAvailable);
        validate(item);
        final Item updatedItem = repository.update(item);
        log.info("Updated item with id = {}: {}", id, updatedItem);
        return updatedItem;
    }

    @Override
    public void deleteItem(final long id, final long userId) {
        repository.findById(id)
                .filter(item -> !Objects.equals(item.getOwnerId(), userId))
                .ifPresent(item -> {
                    throw new ActionNotAllowedException("Only owner can delete item");
                });
        if (repository.delete(id)) {
            log.info("Deleted item with id = {}", id);
        } else {
            log.info("No item deleted: item with id = {} doe not exist", id);
        }
    }

    private void validate(final Item item) {
        Set<ConstraintViolation<Item>> violations = validator.validate(item);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
