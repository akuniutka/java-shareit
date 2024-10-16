package ru.practicum.shareit.booking;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.Objects;

final class BookingUtils {

    private BookingUtils() {
    }

    static Booking makeTestBooking() {
        final Booking booking = new Booking();
        booking.setId(null);
        booking.setItem(new Item());
        booking.getItem().setId(13L);
        booking.setBooker(new User());
        booking.getBooker().setId(42L);
        booking.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        booking.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        booking.setStatus(BookingStatus.WAITING);
        return booking;
    }

    static BookingRetrieveDto makeTestBookingRetrieveDto() {
        final BookingRetrieveDto dto = new BookingRetrieveDto();
        dto.setId(1L);
        dto.setItem(new BookingItemRetrieveDto());
        dto.getItem().setId(13L);
        dto.getItem().setName("The thing");
        dto.setBooker(new BookingBookerRetrieveDto());
        dto.getBooker().setId(42L);
        dto.setStart(LocalDateTime.of(2001, Month.JUNE, 1, 9, 10, 11));
        dto.setEnd(LocalDateTime.of(2001, Month.JUNE, 30, 10, 11, 12));
        dto.setStatus("APPROVED");
        return dto;
    }

    static Booking makeBookingProxy() {
        final Booking booking = new BookingProxy();
        booking.setId(1L);
        booking.setItem(new Item());
        booking.getItem().setId(13L);
        booking.getItem().setName("The thing");
        booking.setBooker(new User());
        booking.getBooker().setId(42L);
        booking.setStart(LocalDateTime.of(2001, Month.JUNE, 1, 9, 10, 11));
        booking.setEnd(LocalDateTime.of(2001, Month.JUNE, 30, 10, 11, 12));
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }

    static BookingCreateDto makeBookingCreateDtoProxy() {
        final BookingCreateDto dto = new BookingCreateDtoProxy();
        dto.setItemId(13L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        return dto;
    }

    static BookingRetrieveDto makeBookingRetrieveDtoProxy() {
        final BookingRetrieveDto dto = new BookingRetrieveDtoProxy();
        dto.setId(1L);
        dto.setItem(new BookingItemRetrieveDto());
        dto.getItem().setId(13L);
        dto.setBooker(new BookingBookerRetrieveDto());
        dto.getBooker().setId(42L);
        dto.setStart(LocalDateTime.of(2000, Month.JULY, 1, 13, 10, 25));
        dto.setEnd(LocalDateTime.of(2000, Month.JULY, 31, 12, 50, 55));
        dto.setStatus("WAITING");
        return dto;
    }

    static Matcher<BookingRetrieveDto> samePropertyValuesAs(final BookingRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final BookingRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final BookingRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getItem(), actual.getItem())
                        && (Objects.isNull(expected.getItem())
                                || Objects.equals(expected.getItem().getName(), actual.getItem().getName()))
                        && Objects.equals(expected.getBooker(), actual.getBooker())
                        && Objects.equals(expected.getStart(), actual.getStart())
                        && Objects.equals(expected.getEnd(), actual.getEnd())
                        && Objects.equals(expected.getStatus(), actual.getStatus());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
