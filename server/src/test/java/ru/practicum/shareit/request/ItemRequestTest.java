package ru.practicum.shareit.request;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequest;

class ItemRequestTest {

    @Test
    void testToString() {
        final String expected = "ItemRequest(id=1, description=Need the thing, created=2001-01-01T00:00:01, "
                + "requester=42)";
        final ItemRequest itemRequest = makeTestItemRequest();
        itemRequest.setId(1L);

        final String actual = itemRequest.toString();

        assertThat(actual, equalTo(expected));
    }
}