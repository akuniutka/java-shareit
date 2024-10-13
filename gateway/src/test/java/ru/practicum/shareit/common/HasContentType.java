package ru.practicum.shareit.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;
import java.util.Optional;

public class HasContentType extends TypeSafeMatcher<HttpStatusCodeException> {

    private final MediaType contentType;

    private HasContentType(final MediaType contentType) {
        this.contentType = contentType;
    }

    @Override
    protected boolean matchesSafely(final HttpStatusCodeException exception) {
        final MediaType testedValue = Optional.ofNullable(exception)
                .map(HttpStatusCodeException::getResponseHeaders)
                .map(HttpHeaders::getContentType)
                .orElse(null);
        return Objects.equals(testedValue, contentType);
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(contentType);
    }

    public static Matcher<HttpStatusCodeException> hasContentType(final MediaType contentType) {
        Objects.requireNonNull(contentType);
        return new HasContentType(contentType);
    }
}
