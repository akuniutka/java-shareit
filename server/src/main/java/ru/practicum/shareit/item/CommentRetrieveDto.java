package ru.practicum.shareit.item;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(of = "id")
public class CommentRetrieveDto {

    private Long id;
    private String authorName;
    private String text;
    private LocalDateTime created;
}
