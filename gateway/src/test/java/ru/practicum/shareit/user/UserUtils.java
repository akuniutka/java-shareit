package ru.practicum.shareit.user;

final class UserUtils {

    private UserUtils() {
    }

    static UserCreateDto makeTestUserCreateDto() {
        return UserCreateDto.builder()
                .name("John Doe")
                .email("john_doe@mail.com")
                .build();
    }

    static UserUpdateDto makeTestUserUpdateDto() {
        return UserUpdateDto.builder()
                .name("John Doe")
                .email("john_doe@mail.com")
                .build();
    }
}
