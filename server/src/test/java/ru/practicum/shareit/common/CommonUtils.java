package ru.practicum.shareit.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.item.Comment;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

public final class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private CommonUtils() {
    }

    public static User makeTestNewUser() {
        final User user = new User();
        user.setId(null);
        user.setName("John Doe");
        user.setEmail("john_doe@mail.com");
        return user;
    }

    public static User makeTestSavedUser() {
        final User user = new User();
        user.setId(42L);
        user.setName("Mr Nobody");
        user.setEmail("nobody@nowhere.com");
        return user;
    }

    public static Item makeTestNewItem() {
        final Item item = new Item();
        item.setId(null);
        item.setOwner(makeTestSavedUser());
        item.setName("The thing");
        item.setDescription("Something from out there");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(null);
        return item;
    }

    public static Item makeTestSavedItem() {
        final Item item = new Item();
        item.setId(13L);
        item.setOwner(makeTestSavedUser());
        item.setName("The next big thing");
        item.setDescription("This thing is ever stranger");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(null);
        return item;
    }

    public static Comment makeTestNewComment() {
        final Comment comment = new Comment();
        comment.setId(null);
        comment.setItem(makeTestSavedItem());
        comment.setAuthor(makeTestSavedUser());
        comment.setText("This is the first comment");
        comment.setCreated(LocalDateTime.of(2000, Month.DECEMBER, 31, 23, 59, 59));
        return comment;
    }

    public static Comment makeTestSavedComment() {
        final Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(makeTestSavedItem());
        comment.setAuthor(makeTestSavedUser());
        comment.setText("This is the first comment");
        comment.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        return comment;
    }

    public static Matcher<User> deepEqualTo(final User user) {
        return new TypeSafeMatcher<>() {

            private final User expected = user;

            @Override
            protected boolean matchesSafely(final User actual) {
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

    public static Matcher<Item> deepEqualTo(final Item item) {
        return new TypeSafeMatcher<>() {

            private final Item expected = item;

            @Override
            protected boolean matchesSafely(final Item actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getOwner(), actual.getOwner())
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getAvailable(), actual.getAvailable())
                        && Objects.equals(expected.getLastBooking(), actual.getLastBooking())
                        && Objects.equals(expected.getNextBooking(), actual.getNextBooking())
                        && Objects.equals(expected.getComments(), actual.getComments())
                        && Objects.equals(expected.getRequest(), actual.getRequest());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(item);
            }
        };
    }

    public static Matcher<Comment> deepEqualTo(final Comment comment) {
        return new TypeSafeMatcher<>() {

            private final Comment expected = comment;

            @Override
            protected boolean matchesSafely(final Comment actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getItem(), actual.getItem())
                        && Objects.equals(expected.getAuthor(), actual.getAuthor())
                        && Objects.equals(expected.getText(), actual.getText())
                        && Objects.equals(expected.getCreated(), actual.getCreated());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    public static String loadJson(final String filename, final Class<?> clazz) throws IOException {
        final String expandedFilename = clazz.getSimpleName().toLowerCase() + "/" + filename;
        final ClassPathResource resource = new ClassPathResource(expandedFilename, clazz);
        return Files.readString(resource.getFile().toPath());
    }

    public static void assertLogs(final List<LogListener.Event> events, final String filename,
            final Class<?> clazz) throws IOException, JSONException {
        final String expected = loadJson(filename, clazz);
        final String actual = mapper.writeValueAsString(events);
        JSONAssert.assertEquals(expected, actual, false);
    }
}
