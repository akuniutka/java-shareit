package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBooking;

class BookingTest {

    @Test
    void testToString() {
        final String expected = "Booking(id=null, start=2000-07-01T13:10:25, end=2000-07-31T12:50:55, status=WAITING, "
                + "item=13, booker=42)";

        final String actual = makeTestBooking().toString();

        assertThat(actual, equalTo(expected));
    }
}