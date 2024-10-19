package ru.practicum.shareit.user;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import static ru.practicum.shareit.common.CommonUtils.USER_ID;

final class UserUtils {

    private UserUtils() {
    }

    static UserProxy makeTestUser() {
        final UserProxy user = new UserProxy();
        user.setId(USER_ID);
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    static UserPatch makeTestUserPatch() {
        final UserPatch patch = new UserPatch();
        patch.setUserId(USER_ID);
        patch.setName("John Doe");
        patch.setEmail("john_doe@mail.com");
        return patch;
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

    static UserRetrieveDto makeTestUserRetrieveDto() {
        return UserRetrieveDto.builder()
                .id(USER_ID)
                .name("John Doe")
                .email("john_doe@mail.com")
                .build();
    }

    static <T extends User> Matcher<T> deepEqualTo(final UserProxy user) {
        return new TypeSafeMatcher<>() {

            private final UserProxy expected = user;

            @Override
            protected boolean matchesSafely(final T actual) {
                return expected.equals(actual);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
