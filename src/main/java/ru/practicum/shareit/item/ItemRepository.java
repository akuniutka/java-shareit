package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ItemRepository extends JpaRepository<Item, Long> {

    Optional<Item> findById(long id);

    List<Item> findByOwnerId(long ownerId);

    @Query("select i from Item i where i.available = true and (lower(i.name) like concat('%', lower(:text), '%') or "
            + "lower(i.description) like concat('%', lower(:text), '%'))")
    List<Item> findByNameOrDescription(@Param("text") String text);

    @Modifying
    @Query("delete from Item i where i.id = :id")
    int delete(@Param("id") long id);

    boolean existsByOwnerId(long userId);
}
