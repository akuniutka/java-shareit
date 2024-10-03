package ru.practicum.shareit.user;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(of = {"id"})
class UserRetrieveDto {

    private Long id;
    private String name;
    private String email;
}
