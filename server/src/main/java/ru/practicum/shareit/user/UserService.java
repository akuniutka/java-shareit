package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    User createUser(User user);

    User getUser(long id);

    List<User> getAllUsers();

    boolean existsById(long id);

    User updateUser(long id, User user);

    void deleteUser(long id);
}
