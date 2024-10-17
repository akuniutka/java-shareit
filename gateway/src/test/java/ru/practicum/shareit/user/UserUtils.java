package ru.practicum.shareit.user;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

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
}
