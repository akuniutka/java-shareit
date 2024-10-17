package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.item.ItemUtils.makeTestItem;

class ItemTest {

    @Test
    void testToString() {
        final String expected = "Item(id=null, name=The thing, description=Something from out there, available=false, "
                + "owner=42, lastBooking=null, nextBooking=null, request=7)";

        final String actual = makeTestItem().toString();

        assertThat(actual, equalTo(expected));
    }
}