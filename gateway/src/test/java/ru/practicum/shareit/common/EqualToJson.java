package ru.practicum.shareit.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.json.JSONException;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.Objects;

public class EqualToJson extends TypeSafeMatcher<Object> {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    private final String json;

    private EqualToJson(final String json) {
        this.json = json;
    }

    @Override
    protected boolean matchesSafely(final Object o) {
        try {
            if (o == null) {
                return false;
            }
            final String testedString;
            if (o instanceof String s) {
                testedString = s;
            } else {
                testedString = objectMapper.writeValueAsString(o);
            }
            JSONAssert.assertEquals(json, testedString, true);
            return true;
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Cannot convert value to JSON");
        } catch (JSONException e) {
            throw new RuntimeException("Cannot process JSON");
        } catch (AssertionError e) {
            return false;
        }
    }

    @Override
    public void describeTo(final Description description) {
        description.appendText("equivalent of " + json);
    }

    public static Matcher<Object> equalToJson(final String json) {
        Objects.requireNonNull(json);
        return new EqualToJson(json);
    }
}
