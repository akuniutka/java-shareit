package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingCreateDtoProxy;
import static ru.practicum.shareit.booking.BookingUtils.makeBookingProxy;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBooking;
import static ru.practicum.shareit.booking.BookingUtils.makeTestBookingRetrieveDto;
import static ru.practicum.shareit.booking.BookingUtils.samePropertyValuesAs;

class BookingMapperImplTest {

    private BookingMapperImpl mapper;

    @BeforeEach
    void setUp() {
        mapper = new BookingMapperImpl();
    }

    @Test
    void testMapToBooking() {
        final Booking expected = makeTestBooking();

        final Booking actual = mapper.mapToBooking(makeBookingCreateDtoProxy(), 42L);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToBookingWhenUserIdNull() {
        final Booking expected = makeTestBooking();
        expected.setBooker(null);

        final Booking actual = mapper.mapToBooking(makeBookingCreateDtoProxy(), null);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToBookingWhenDtoIsNull() {
        final Booking expected = makeTestBooking();
        expected.setItem(null);
        expected.setStart(null);
        expected.setEnd(null);

        final Booking actual = mapper.mapToBooking(null, 42L);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToBookingWhenUserIdNullAndDtoNull() {
        final Booking actual = mapper.mapToBooking(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleBooking() {
        final BookingRetrieveDto expected = makeTestBookingRetrieveDto();

        final BookingRetrieveDto actual = mapper.mapToDto(makeBookingProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleBookingNull() {
        final BookingRetrieveDto actual = mapper.mapToDto((Booking) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleBookingAndItemNull() {
        final BookingRetrieveDto expected = makeTestBookingRetrieveDto();
        expected.setItem(null);
        final Booking booking = makeBookingProxy();
        booking.setItem(null);

        final BookingRetrieveDto actual = mapper.mapToDto(booking);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleBookingAndBookerNull() {
        final BookingRetrieveDto expected = makeTestBookingRetrieveDto();
        expected.setBooker(null);
        final Booking booking = makeBookingProxy();
        booking.setBooker(null);

        final BookingRetrieveDto actual = mapper.mapToDto(booking);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleBookingAndStatusNull() {
        final BookingRetrieveDto expected = makeTestBookingRetrieveDto();
        expected.setStatus(null);
        final Booking booking = makeBookingProxy();
        booking.setStatus(null);

        final BookingRetrieveDto actual = mapper.mapToDto(booking);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenBookingList() {
        final BookingRetrieveDto expected = makeTestBookingRetrieveDto();

        final List<BookingRetrieveDto> actual = mapper.mapToDto(List.of(makeBookingProxy()));

        assertThat(actual, contains(samePropertyValuesAs(expected)));
    }

    @Test
    void testMapToDtoWhenBookingListNull() {
        final List<BookingRetrieveDto> actual = mapper.mapToDto((List<Booking>) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testUnnecessaryMapStructNullCheck() {
        final Item actual = mapper.bookingCreateDtoToItem(null);

        assertThat(actual, nullValue());
    }
}