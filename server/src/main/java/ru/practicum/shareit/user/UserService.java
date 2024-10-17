package ru.practicum.shareit.user;

import jakarta.validation.Valid;

import java.util.List;

public interface UserService {

    User createUser(@Valid User user);

    User getUser(long id);

    List<User> getAllUsers();

    boolean existsById(long id);

    User patchUser(@Valid UserPatch patch);

    void deleteUser(long id);
}
