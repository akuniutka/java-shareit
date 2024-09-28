package ru.practicum.shareit.item;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.Optional;

@Entity
@Table(name = "comments")
@Data
@EqualsAndHashCode(of = "id")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull
    private User author;

    @NotBlank
    @Size(max = 2000)
    private String text;

    @NotNull
    private LocalDateTime created = LocalDateTime.now();

    // To avoid circular reference in toString()
    @ToString.Include
    public Long item() {
        return Optional.ofNullable(item).map(Item::getId).orElse(null);
    }

    // To avoid circular reference in toString()
    @ToString.Include
    public Long author() {
        return Optional.ofNullable(author).map(User::getId).orElse(null);
    }
}
