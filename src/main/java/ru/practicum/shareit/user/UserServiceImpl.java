package ru.practicum.shareit.user;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.common.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository repository;
    private final Validator validator;

    @Override
    public User createUser(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
        validate(user);
        final User createdUser = repository.save(user);
        log.info("Created user with id = {}: {}", createdUser.getId(), createdUser);
        return createdUser;
    }

    @Override
    public User getUser(final long id) {
        return repository.findById(id).orElseThrow(
                () -> new NotFoundException(User.class, id)
        );
    }

    @Override
    public List<User> getAllUsers() {
        return repository.findAll();
    }

    @Override
    public User updateUser(final long id, final User update) {
        Objects.requireNonNull(update, "Cannot update user: is null");
        final User user = repository.findById(id).orElseThrow(
                () -> new NotFoundException(User.class, id)
        );
        Optional.ofNullable(update.getName()).ifPresent(user::setName);
        Optional.ofNullable(update.getEmail()).ifPresent(user::setEmail);
        validate(user);
        final User updatedUser = repository.update(user);
        log.info("Updated user with id = {}: {}", id, updatedUser);
        return updatedUser;
    }

    @Override
    public void deleteUser(final long id) {
        if (repository.delete(id)) {
            log.info("Deleted user with id = {}", id);
        } else {
            log.info("No user deleted: user with id = {} does not exist", id);
        }
    }

    private void validate(final User user) {
        Set<ConstraintViolation<User>> violations = validator.validate(user);
        if (!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
