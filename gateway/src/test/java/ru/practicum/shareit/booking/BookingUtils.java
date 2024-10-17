package ru.practicum.shareit.booking;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

final class BookingUtils {

    private BookingUtils() {
    }

    static BookingCreateDto makeTestBookingCreateDto() {
        final BookingCreateDto dto = new BookingCreateDto();
        dto.setItemId(13L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        return dto;
    }

    static Matcher<BookingCreateDto> deepEqualTo(final BookingCreateDto dto) {
        return new TypeSafeMatcher<>() {

            private final BookingCreateDto expected = dto;

            @Override
            protected boolean matchesSafely(final BookingCreateDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getItemId(), actual.getItemId())
                        && Objects.equals(expected.getStart(), actual.getStart())
                        && Objects.equals(expected.getEnd(), actual.getEnd());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
