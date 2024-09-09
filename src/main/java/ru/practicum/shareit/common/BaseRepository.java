package ru.practicum.shareit.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class BaseRepository<T> {

    protected final Map<Long, T> data;
    protected final Function<T, Long> idGetter;
    protected final BiConsumer<T, Long> idSetter;
    protected final EntityCopier<T> copier;
    protected long lastUsedId;

    public BaseRepository(final Function<T, Long> idGetter, final BiConsumer<T, Long> idSetter,
            final EntityCopier<T> copier) {
        this.data = new HashMap<>();
        this.idGetter = idGetter;
        this.idSetter = idSetter;
        this.copier = copier;
        this.lastUsedId = 0L;
    }

    public T save(final T entity) {
        Objects.requireNonNull(entity, "Cannot save entity: is null");
        final T copy = copier.copy(entity);
        lastUsedId++;
        idSetter.accept(copy, lastUsedId);
        return persist(lastUsedId, copy);
    }

    public Optional<T> findById(final long id) {
        return Optional.ofNullable(data.get(id));
    }

    public List<T> findAll() {
        return data.values().stream()
                .toList();
    }

    public T update(final T entity) {
        Objects.requireNonNull(entity, "Cannot update entity: is null");
        final Long id = idGetter.apply(entity);
        Objects.requireNonNull(id, "Cannot update entity: entity id is null");
        Objects.requireNonNull(data.get(id), "Cannot update entity: unknown id (%s)".formatted(id));
        final T copy = copier.copy(entity);
        return persist(id, copy);
    }

    public boolean delete(final long id) {
        return data.remove(id) != null;
    }

    protected T persist(final long id, final T entity) {
        data.put(id, entity);
        return entity;
    }
}
