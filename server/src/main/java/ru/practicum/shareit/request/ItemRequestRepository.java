package ru.practicum.shareit.request;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("select r from ItemRequest r left join fetch r.items where r.id = :id")
    Optional<ItemRequest> findByIdWithRelations(@Param("id") long id);

    @Query("select r from ItemRequest r left join fetch r.items where r.requester.id = :userId")
    List<ItemRequest> findAllByRequesterId(@Param("userId") long userId, Pageable page);

    @Query("select r from ItemRequest r left join fetch r.items where r.requester.id != :userId")
    List<ItemRequest> findAllOtherByRequesterId(@Param("userId") long userId, Pageable page);
}
