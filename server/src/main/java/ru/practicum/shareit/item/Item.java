package ru.practicum.shareit.item;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.Request;
import ru.practicum.shareit.user.User;

import java.util.Optional;
import java.util.Set;

@Entity
@Table(name = "items")
@Data
@EqualsAndHashCode(of = {"id"})
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User owner;

    @NotBlank
    @Size(max = 255)
    private String name;

    @NotBlank
    @Size(max = 2000)
    private String description;

    @NotNull
    private Boolean available;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "last_bookings",
            joinColumns = @JoinColumn(name = "item_id", updatable = false, insertable = false),
            inverseJoinColumns = @JoinColumn(name = "booking_id", updatable = false, insertable = false))
    private Booking lastBooking;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "next_bookings",
            joinColumns = @JoinColumn(name = "item_id", updatable = false, insertable = false),
            inverseJoinColumns = @JoinColumn(name = "booking_id", updatable = false, insertable = false))
    private Booking nextBooking;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    @ToString.Exclude
    private Set<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    private Request request;

    // To avoid circular reference in toString()
    @ToString.Include
    public Long owner() {
        return Optional.ofNullable(owner).map(User::getId).orElse(null);
    }

    // To avoid circular reference in toString()
    @ToString.Include
    public Long lastBooking() {
        return Optional.ofNullable(lastBooking).map(Booking::getId).orElse(null);
    }

    // To avoid circular reference in toString()
    @ToString.Include
    public Long nextBooking() {
        return Optional.ofNullable(nextBooking).map(Booking::getId).orElse(null);
    }

    // To avoid circular reference in toString()
    @ToString.Include
    public Long request() {
        return Optional.ofNullable(request).map(Request::getId).orElse(null);
    }
}
