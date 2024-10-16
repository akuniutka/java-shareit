package ru.practicum.shareit.item;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Objects;

final class ItemUtils {

    private ItemUtils() {
    }

    static Comment makeTestComment() {
        final Comment comment = new Comment();
        comment.setId(null);
        comment.setItem(new Item());
        comment.getItem().setId(13L);
        comment.setAuthor(new User());
        comment.getAuthor().setId(42L);
        comment.setText("This is the first comment");
        comment.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return comment;
    }

    static CommentRetrieveDto makeTestCommentRetrieveDto() {
        final CommentRetrieveDto dto = new CommentRetrieveDto();
        dto.setId(1L);
        dto.setAuthorName("John Doe");
        dto.setText("This is the first comment");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return dto;
    }

    static Item makeItemProxy() {
        final Item item = new ItemProxy();
        item.setId(13L);
        item.setOwner(new User());
        item.getOwner().setId(42L);
        item.setName("The next big thing");
        item.setDescription("This thing is ever stranger");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(null);
        return item;
    }

    static Comment makeCommentProxy() {
        final Comment comment = new CommentProxy();
        comment.setId(1L);
        comment.setItem(new Item());
        comment.getItem().setId(13L);
        comment.setAuthor(new User());
        comment.getAuthor().setId(42L);
        comment.getAuthor().setName("John Doe");
        comment.setText("This is the first comment");
        comment.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return comment;
    }

    static ItemCreateDto makeItemCreateDtoProxy() {
        final ItemCreateDto dto = new ItemCreateDtoProxy();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        dto.setRequestId(42L);
        return dto;
    }

    static CommentCreateDto makeCommentCreateDtoProxy() {
        final CommentCreateDto dto = new CommentCreateDtoProxy();
        dto.setText("This is the first comment");
        return dto;
    }

    static CommentRetrieveDto makeCommentRetrieveDtoProxy() {
        final CommentRetrieveDto dto = new CommentRetrieveDtoProxy();
        dto.setId(5L);
        dto.setAuthorName("John Doe");
        dto.setText("This is the first comment");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return dto;
    }

    static ItemUpdateDto makeItemUpdateDtoProxy() {
        final ItemUpdateDto dto = new ItemUpdateDtoProxy();
        dto.setName("The thing");
        dto.setDescription("Something from out there");
        dto.setAvailable(false);
        return dto;
    }

    static ItemRetrieveDto makeItemRetrieveDtoProxy() {
        final ItemRetrieveDto dto = new ItemRetrieveDtoProxy();
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

    static Matcher<CommentRetrieveDto> samePropertyValuesAs(final CommentRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final CommentRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final CommentRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
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
