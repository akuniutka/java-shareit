package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class UserUtils {

    private UserUtils() {
    }

    static void assertUserEqual(final User expected, final User actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }

    static User makeUserProxy() {
        final User user = new UserProxy();
        user.setId(42L);
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    static UserPatch makeUserPatchProxy() {
        final UserPatch patch = new UserPatchProxy();
        patch.setUserId(42L);
        patch.setName("Mr Nobody");
        patch.setEmail("nobody@nowhere.com");
        return patch;
    }

    static UserCreateDto makeUserCreateDtoProxy() {
        final UserCreateDto dto = new UserCreateDtoProxy();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static UserUpdateDto makeUserUpdateDtoProxy() {
        final UserUpdateDto dto = new UserUpdateDtoProxy();
        dto.setName("Mr Nobody");
        dto.setEmail("nobody@nowhere.com");
        return dto;
    }

    static UserRetrieveDto makeUserRetrieveDtoProxy() {
        final UserRetrieveDto dto = new UserRetrieveDtoProxy();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }
}
