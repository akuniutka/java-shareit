package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

final class TestUtils {

    private TestUtils() {
    }

    static User makeTestNewUser() {
        final User user = new User();
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    static User makeTestSavedUser() {
        final User user = new User();
        user.setId(42L);
        user.setName("Mr Nobody");
        user.setEmail("nobody@nowhere.com");
        return user;
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

    static UserPatch makeTestUserPatch() {
        final UserPatch patch = new UserPatch();
        patch.setUserId(42L);
        patch.setName("Mr Nobody");
        patch.setEmail("nobody@nowhere.com");
        return patch;
    }

    static void assertUserPatchEqual(final UserPatch expected, final UserPatch actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getUserId(), actual.getUserId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }

    static UserCreateDto makeTestUserCreateDto() {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static void assertCreateDtoEqual(final UserCreateDto expected, final UserCreateDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }

    static UserUpdateDto makeTestUserUpdateDto() {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Mr Nobody");
        dto.setEmail("nobody@nowhere.com");
        return dto;
    }

    static void assertUpdateDtoEqual(final UserUpdateDto expected, final UserUpdateDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }

    static UserRetrieveDto makeTestUserRetrieveDto() {
        final UserRetrieveDto dto = new UserRetrieveDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static void assertRetrieveDtoEqual(final UserRetrieveDto expected, final UserRetrieveDto actual) {
        assertNotNull(expected);
        assertNotNull(actual);
        assertAll(
                () -> assertEquals(expected.getId(), actual.getId()),
                () -> assertEquals(expected.getName(), actual.getName()),
                () -> assertEquals(expected.getEmail(), actual.getEmail())
        );
    }
}
