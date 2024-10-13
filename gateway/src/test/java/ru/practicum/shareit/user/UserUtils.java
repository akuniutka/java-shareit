package ru.practicum.shareit.user;

final class UserUtils {

    private UserUtils() {
    }

    static UserCreateDto makeTestUserCreateDto() {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static UserUpdateDto makeTestUserUpdateDto() {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }
}
