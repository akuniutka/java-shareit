package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequest;

class RequestTest {

    @Test
    void testToString() {
        final String expected = "Request(id=7, description=Need the thing, created=2001-01-01T00:00:01, "
                + "requester=42)";
        final Request request = makeTestRequest();

        final String actual = request.toString();

        assertThat(actual, equalTo(expected));
    }
}