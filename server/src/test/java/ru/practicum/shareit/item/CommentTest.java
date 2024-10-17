package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.item.ItemUtils.makeTestComment;

class CommentTest {

    @Test
    void testToString() {
        final String expected = "Comment(id=null, text=This is the first comment, created=2001-01-01T00:00:01, item=13,"
                + " author=42)";

        final String actual = makeTestComment().toString();

        assertThat(actual, equalTo(expected));
    }
}