package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

interface UserRepository {

    User save(User user);

    Optional<User> findById(long id);

    List<User> findAll();

    User update(User user);

    boolean delete(long id);
}
