package ru.practicum.shareit.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;
import java.util.Optional;

public class ErrorResponseMatchers extends TypeSafeMatcher<HttpStatusCodeException> {

    private final HttpStatus status;
    private final String jsonBody;

    private ErrorResponseMatchers(final HttpStatus status, final String jsonBody) {
        this.status = status;
        this.jsonBody = jsonBody;
    }

    public static Matcher<HttpStatusCodeException> isErrorResponse(final HttpStatus status, final String jsonBody) {
        Objects.requireNonNull(status);
        Objects.requireNonNull(jsonBody);
        return new ErrorResponseMatchers(status, jsonBody);
    }

    public static Matcher<HttpStatusCodeException> isBadRequest(final String jsonBody) {
        return isErrorResponse(HttpStatus.BAD_REQUEST, jsonBody);
    }

    public static Matcher<HttpStatusCodeException> isConflict(final String jsonBody) {
        return isErrorResponse(HttpStatus.CONFLICT, jsonBody);
    }

    public static Matcher<HttpStatusCodeException> isNotFound(final String jsonBody) {
        return isErrorResponse(HttpStatus.NOT_FOUND, jsonBody);
    }

    public static Matcher<HttpStatusCodeException> isInternalServerError(final String jsonBody) {
        return isErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, jsonBody);
    }

    public static Matcher<HttpStatusCodeException> isForbidden(final String jsonBody) {
        return isErrorResponse(HttpStatus.FORBIDDEN, jsonBody);
    }

    @Override
    protected boolean matchesSafely(final HttpStatusCodeException exception) {
        if (exception == null || !Objects.equals(status, exception.getStatusCode())) {
            return false;
        }
        final MediaType contentType = Optional.ofNullable(exception.getResponseHeaders())
                .map(HttpHeaders::getContentType)
                .orElse(null);
        if (!Objects.equals(MediaType.APPLICATION_PROBLEM_JSON, contentType)) {
            return false;
        }
        final String actualBody = exception.getResponseBodyAsString();
        try {
            JSONAssert.assertEquals(jsonBody, actualBody, true);
            return true;
        } catch (JSONException e) {
            throw new RuntimeException("Cannot process JSON");
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("%s and %s".formatted(status, jsonBody));
    }
}
