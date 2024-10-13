package ru.practicum.shareit.common;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.web.client.HttpStatusCodeException;

import java.util.Objects;

public class HasJsonBody extends TypeSafeMatcher<HttpStatusCodeException> {

    private final String json;

    private HasJsonBody(final String json) {
        this.json = json;
    }

    @Override
    protected boolean matchesSafely(final HttpStatusCodeException exception) {
        if (exception == null) {
            return false;
        }
        final String testedValue = exception.getResponseBodyAsString();
        try {
            JSONAssert.assertEquals(json, testedValue, true);
            return true;
        } catch (JSONException e) {
            throw new RuntimeException("Cannot process JSON");
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendValue(json);
    }

    public static Matcher<HttpStatusCodeException> hasJsonBody(final String json) {
        Objects.requireNonNull(json);
        return new HasJsonBody(json);
    }
}
