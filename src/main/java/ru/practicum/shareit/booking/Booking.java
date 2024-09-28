package ru.practicum.shareit.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "bookings")
@Data
@EqualsAndHashCode(of = "id")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @NotNull
    private Item item;

    // To avoid circular reference in toString()
    @ToString.Include
    public Long item() {
        return Optional.ofNullable(item).map(Item::getId).orElse(null);
    }

    @ManyToOne
    @NotNull
    private User booker;

    // To avoid circular reference in toString()
    @ToString.Include
    public Long booker() {
        return Optional.ofNullable(booker).map(User::getId).orElse(null);
    }

    @NotNull
    @Column(name = "booking_start")
    private LocalDateTime start;

    @NotNull
    @Column(name = "booking_end")
    private LocalDateTime end;

    @Enumerated(EnumType.STRING)
    @NotNull
    private BookingStatus status;
}
