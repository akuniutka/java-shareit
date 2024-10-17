package ru.practicum.shareit.user;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

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

    static User makeTestUser() {
        final User user = new User();
        user.setId(null);
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    static UserPatch makeTestUserPatch() {
        final UserPatch patch = new UserPatch();
        patch.setUserId(42L);
        patch.setName("Mr Nobody");
        patch.setEmail("nobody@nowhere.com");
        return patch;
    }

    static UserRetrieveDto makeTestUserRetrieveDto() {
        final UserRetrieveDto dto = new UserRetrieveDto();
        dto.setId(42L);
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
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

    static Matcher<UserRetrieveDto> samePropertyValuesAs(final UserRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final UserRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final UserRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getEmail(), actual.getEmail());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
