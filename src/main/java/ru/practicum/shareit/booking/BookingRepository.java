package ru.practicum.shareit.booking;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("select b from Booking b join fetch b.booker join fetch b.item join fetch b.item.owner where b.id = :id")
    Optional<Booking> findByIdWithBookerAndItemOwner(@Param("id") long id);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner where b.id = :id and "
            + "(b.booker.id = :userId or b.item.owner.id = :userId)")
    Optional<Booking> findByIdAndBookerIdOrItemOwnerId(@Param("id") long id, @Param("userId") long userId);

    @Query("select b from Booking b join fetch b.booker join fetch b.item where b.booker.id = :userId")
    List<Booking> findAllByBookerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item where b.booker.id = :userId "
            + "and b.start <= current_timestamp and b.end > current_timestamp")
    List<Booking> findCurrentByBookerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item where b.booker.id = :userId "
            + "and b.end <= current_timestamp")
    List<Booking> findPastByBookerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item where b.booker.id = :userId "
            + "and b.start > current_timestamp")
    List<Booking> findFutureByBookerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item where b.booker.id = :userId "
            + "and b.status = :status")
    List<Booking> findAllByBookerIdAndStatus(@Param("userId") long userId, @Param("status") BookingStatus status,
            Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner "
            + "where b.item.owner.id = :userId")
    List<Booking> findAllByItemOwnerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner "
            + "where b.item.owner.id = :userId and b.start <= current_timestamp and b.end > current_timestamp")
    List<Booking> findCurrentByItemOwnerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner "
            + "where b.item.owner.id = :userId and b.end <= current_timestamp")
    List<Booking> findPastByItemOwnerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner "
            + "where b.item.owner.id = :userId and b.start > current_timestamp")
    List<Booking> findFutureByItemOwnerId(@Param("userId") long userId, Sort sort);

    @Query("select b from Booking b join fetch b.booker join fetch b.item join b.item.owner "
            + "where b.item.owner.id = :userId and b.status = :status")
    List<Booking> findAllByItemOwnerIdAndStatus(@Param("userId") long userId, @Param("status") BookingStatus status,
            Sort sort);
}
