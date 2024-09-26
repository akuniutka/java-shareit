package ru.practicum.shareit.user;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.common.BaseRepository;
import ru.practicum.shareit.common.exception.DuplicateDataException;

import java.util.HashSet;
import java.util.Set;

@Repository
class UserRepositoryImpl extends BaseRepository<User> implements UserRepository {

    private final Set<String> uniqueEmails;

    public UserRepositoryImpl(final UserMapper mapper) {
        super(User::getId, User::setId, mapper);
        this.uniqueEmails = new HashSet<>();
    }

    @Override
    public boolean delete(final long id) {
        final String prevEmail = findById(id).map(User::getEmail).map(String::toLowerCase).orElse(null);
        final boolean isDeleted = super.delete(id);
        uniqueEmails.remove(prevEmail);
        return isDeleted;
    }

    @Override
    protected User persist(final long id, final User user) {
        final String newEmail = user.getEmail().toLowerCase();
        final String prevEmail = findById(id).map(User::getEmail).map(String::toLowerCase).orElse(null);
        if (uniqueEmails.contains(newEmail) && !newEmail.equals(prevEmail)) {
            throw new DuplicateDataException("Email %s already exists".formatted(user.getEmail()));
        }
        final User saveduser = super.persist(id, user);
        uniqueEmails.add(newEmail);
        return saveduser;
    }
}
