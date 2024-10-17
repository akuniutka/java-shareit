package ru.practicum.shareit.item;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ItemRepository extends JpaRepository<Item, Long> {

    @Query("select i from Item i left join fetch i.comments c left join fetch c.author where i.id = :id")
    Optional<Item> findByIdWithRelations(@Param("id") long id);

    @Query("select i from Item i left join fetch i.comments c left join fetch c.author where i.owner.id = :userId")
    List<Item> findByOwnerId(@Param("userId") long ownerId, Sort sort);

    @Query("select i from Item i left join fetch i.comments c left join c.author where i.available = true "
            + "and (lower(i.name) like concat('%', lower(:text), '%') "
            + "or lower(i.description) like concat('%', lower(:text), '%'))")
    List<Item> findByNameOrDescription(@Param("text") String text, Sort sort);

    @Modifying
    @Query("delete from Item i where i.id = :id")
    int delete(@Param("id") long id);

    boolean existsByOwnerId(long userId);
}
