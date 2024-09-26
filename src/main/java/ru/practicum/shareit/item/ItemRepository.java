package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i join fetch i.owner where i.id = ?1")
    Optional<Item> findByIdWithOwner(long id);

    List<Item> findByOwnerId(long ownerId);

    @Query("select i from Item i where i.available = true and (lower(i.name) like concat('%', lower(?1), '%') or lower(i.description) like concat('%', lower(?1), '%'))")
    List<Item> findByNameOrDescription(String text);

    @Modifying
    @Query("delete from Item i where i.id = ?1")
    int delete(long id);
}
