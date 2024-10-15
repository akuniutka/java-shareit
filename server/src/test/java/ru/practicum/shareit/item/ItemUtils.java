package ru.practicum.shareit.item;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
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

    static CommentRetrieveDto makeTestCommentRetrieveDto() {
        final CommentRetrieveDto dto = new CommentRetrieveDto();
        dto.setId(5L);
        dto.setAuthorName("John Doe");
        dto.setText("This is the first comment");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return dto;
    }

    static ItemUpdateDto makeTestItemUpdateDto() {
        final ItemUpdateDto dto = new ItemUpdateDto();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        return dto;
    }

    static ItemRetrieveDto makeTestItemRetrieveDto() {
        final ItemRetrieveDto dto = new ItemRetrieveDto();
        dto.setId(13L);
        dto.setName("The next big thing");
        dto.setDescription("This thing is ever stranger");
        dto.setAvailable(false);
        dto.setRequestId(null);
        dto.setLastBooking(null);
        dto.setNextBooking(null);
        dto.setComments(new HashSet<>());
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

    static Matcher<ItemRetrieveDto> deepEqualTo(final ItemRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(ItemRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getAvailable(), actual.getAvailable())
                        && Objects.equals(expected.getRequestId(), actual.getRequestId())
                        && Objects.equals(expected.getLastBooking(), actual.getLastBooking())
                        && Objects.equals(expected.getNextBooking(), actual.getNextBooking())
                        && Objects.equals(expected.getComments(), actual.getComments());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<CommentRetrieveDto> deepEqualTo(final CommentRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final CommentRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final CommentRetrieveDto actual) {
                return Objects.nonNull(expected.getId()) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getAuthorName(), actual.getAuthorName())
                        && Objects.equals(expected.getText(), actual.getText())
                        && Objects.equals(expected.getCreated(), actual.getCreated());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
