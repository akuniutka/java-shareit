package ru.practicum.shareit.item;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Objects;

final class ItemUtils {

    private ItemUtils() {
    }

    static Item makeTestItem() {
        final Item item = new Item();
        item.setId(null);
        item.setOwner(new User());
        item.getOwner().setId(42L);
        item.setName("The thing");
        item.setDescription("Something from out there");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(new ItemRequest());
        item.getRequest().setId(7L);
        return item;
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

    static Booking makeTestBooking() {
        final Booking booking = new Booking();
        booking.setId(1L);
        booking.setItem(new Item());
        booking.getItem().setId(13L);
        booking.getItem().setName("The thing");
        booking.setBooker(new User());
        booking.getBooker().setId(42L);
        booking.setStart(LocalDateTime.of(2001, Month.JUNE, 1, 9, 10, 11));
        booking.setEnd(LocalDateTime.of(2001, Month.JUNE, 30, 10, 11, 12));
        booking.setStatus(null);
        return booking;
    }

    static ItemBookingRetrieveDto makeTestItemBookingRetrieveDto() {
        final ItemBookingRetrieveDto dto = new ItemBookingRetrieveDto();
        dto.setId(1L);
        dto.setBookerId(42L);
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
        dto.setRequestId(7L);
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

    static Matcher<ItemRetrieveDto> samePropertyValuesAs(final ItemRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemRetrieveDto actual) {
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

    static Matcher<ItemBookingRetrieveDto> samePropertyValuesAs(final ItemBookingRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemBookingRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemBookingRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getBookerId(), actual.getBookerId());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
