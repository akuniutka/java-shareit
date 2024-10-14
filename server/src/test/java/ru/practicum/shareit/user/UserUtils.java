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

    static UserPatch makeTestUserPatch() {
        final UserPatch patch = new UserPatch();
        patch.setUserId(42L);
        patch.setName("Mr Nobody");
        patch.setEmail("nobody@nowhere.com");
        return patch;
    }

    static UserCreateDto makeTestUserCreateDto() {
        final UserCreateDto dto = new UserCreateDto();
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static UserUpdateDto makeTestUserUpdateDto() {
        final UserUpdateDto dto = new UserUpdateDto();
        dto.setName("Mr Nobody");
        dto.setEmail("nobody@nowhere.com");
        return dto;
    }

    static UserRetrieveDto makeTestUserRetrieveDto() {
        final UserRetrieveDto dto = new UserRetrieveDto();
        dto.setId(1L);
        dto.setName("John Doe");
        dto.setEmail("john_doe@mail.com");
        return dto;
    }

    static Matcher<UserPatch> deepEqualTo(final UserPatch patch) {
        return new TypeSafeMatcher<>() {

            private final UserPatch expected = patch;

            @Override
            protected boolean matchesSafely(final UserPatch actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getUserId(), actual.getUserId())
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getEmail(), actual.getEmail());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<UserCreateDto> deepEqualTo(final UserCreateDto dto) {
        return new TypeSafeMatcher<>() {

            private final UserCreateDto expected = dto;

            @Override
            protected boolean matchesSafely(final UserCreateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getEmail(), actual.getEmail());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<UserUpdateDto> deepEqualTo(final UserUpdateDto dto) {
        return new TypeSafeMatcher<>() {

            private final UserUpdateDto expected = dto;

            @Override
            protected boolean matchesSafely(final UserUpdateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getEmail(), actual.getEmail());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<UserRetrieveDto> deepEqualTo(final UserRetrieveDto dto) {
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
