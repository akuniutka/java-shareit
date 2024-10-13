package ru.practicum.shareit.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

public class HasStatus extends TypeSafeMatcher<HttpStatusCodeException> {

    private final HttpStatus status;

    private HasStatus(final HttpStatus status) {
        this.status = status;
    }

    @Override
    protected boolean matchesSafely(final HttpStatusCodeException exception) {
        if (exception == null) {
            return false;
        }
        return Objects.equals(exception.getStatusCode(), status);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(status);
    }

    public static Matcher<HttpStatusCodeException> hasStatus(final HttpStatus status) {
        Objects.requireNonNull(status);
        return new HasStatus(status);
    }
}
