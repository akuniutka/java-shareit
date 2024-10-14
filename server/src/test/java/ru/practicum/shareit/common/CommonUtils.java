package ru.practicum.shareit.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.core.io.ClassPathResource;
import ru.practicum.shareit.user.User;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;

public final class CommonUtils {

    private static final ObjectMapper mapper = new ObjectMapper();

    private CommonUtils() {
    }

    public static User makeTestNewUser() {
        final User user = new User();
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
