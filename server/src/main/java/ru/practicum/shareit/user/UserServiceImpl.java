package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import ru.practicum.shareit.common.exception.NotFoundException;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@Validated
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Slf4j
class UserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    @Transactional
    public User createUser(final User user) {
        Objects.requireNonNull(user, "Cannot create user: is null");
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
    public boolean existsById(final long id) {
        return repository.existsById(id);
    }

    @Override
    @Transactional
    public User patchUser(final UserPatch patch) {
        Objects.requireNonNull(patch, "Cannot patch user: is null");
        final User user = getUser(patch.getUserId());
        patchUserInternal(user, patch);
        final User updatedUser = repository.save(user);
        log.info("Updated user with id = {}: {}", patch.getUserId(), updatedUser);
        return updatedUser;
    }

    @Override
    @Transactional
    public void deleteUser(final long id) {
        if (repository.delete(id) != 0) {
            log.info("Deleted user with id = {}", id);
        } else {
            log.info("No user deleted: user with id = {} does not exist", id);
        }
    }

    private void patchUserInternal(final User user, final UserPatch patch) {
        Optional.ofNullable(patch.getName()).ifPresent(user::setName);
        Optional.ofNullable(patch.getEmail()).ifPresent(user::setEmail);
    }
}
