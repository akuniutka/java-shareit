package ru.practicum.shareit.request;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

final class ItemRequestUtils {

    private ItemRequestUtils() {
    }

    static ItemRequestCreateDto makeTestItemRequestCreateDto() {
        final ItemRequestCreateDto dto = new ItemRequestCreateDto();
        dto.setDescription("Need the thing");
        return dto;
    }

    static Matcher<ItemRequestCreateDto> deepEqualTo(final ItemRequestCreateDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemRequestCreateDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemRequestCreateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getDescription(), actual.getDescription());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
