package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseRepository;

import java.util.Objects;
import java.util.Optional;

@Repository
public class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {

    public UserRepositoryImpl(final UserMapper mapper) {
        super(User::getId, User::setId, mapper);
    }

    @Override
    public Optional<User> findByEmail(final String email) {
        return data.values().stream()
                .filter(u -> Objects.equals(email, u.getEmail()))
                .findAny()
                .map(copier::copy);
    }
}
