package ru.practicum.shareit.item;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.util.Objects;

final class ItemUtils {

    private ItemUtils() {
    }

    static ItemCreateDto makeTestItemCreateDto() {
        final ItemCreateDto dto = new ItemCreateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        dto.setRequestId(42L);
        return dto;
    }

    static CommentCreateDto makeTestCommentCreateDto() {
        final CommentCreateDto dto = new CommentCreateDto();
        dto.setText("This is the first comment");
        return dto;
    }

    static ItemUpdateDto makeTestItemUpdateDto() {
        final ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        return dto;
    }

    static Matcher<ItemCreateDto> deepEqualTo(final ItemCreateDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemCreateDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemCreateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getAvailable(), actual.getAvailable())
                        && Objects.equals(expected.getRequestId(), actual.getRequestId());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<CommentCreateDto> deepEqualTo(final CommentCreateDto dto) {
        return new TypeSafeMatcher<>() {

            private final CommentCreateDto expected = dto;

            @Override
            protected boolean matchesSafely(final CommentCreateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getText(), actual.getText());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<ItemUpdateDto> deepEqualTo(final ItemUpdateDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemUpdateDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemUpdateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getAvailable(), actual.getAvailable());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
