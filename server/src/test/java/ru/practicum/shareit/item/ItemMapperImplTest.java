package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.request.Request;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.shareit.item.ItemUtils.makeItemCreateDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemProxy;
import static ru.practicum.shareit.item.ItemUtils.makeItemUpdateDtoProxy;
import static ru.practicum.shareit.item.ItemUtils.makeTestBooking;
import static ru.practicum.shareit.item.ItemUtils.makeTestItem;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemBookingRetrieveDto;
import static ru.practicum.shareit.item.ItemUtils.makeTestItemRetrieveDto;
import static ru.practicum.shareit.item.ItemUtils.samePropertyValuesAs;

class ItemMapperImplTest {

    private static final long USER_ID = 42L;

    private ItemMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemMapperImpl();
        final CommentMapper commentMapper = new CommentMapperImpl();
        ReflectionTestUtils.setField(mapper, "commentMapper", commentMapper);
    }

    @Test
    void testMapToItemWhenItemCreateDto() {
        final Item expected = makeTestItem();

        final Item actual = mapper.mapToItem(USER_ID, makeItemCreateDtoProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToItemWhenItemCreateDtoAndRequestIdNull() {
        final Item expected = makeTestItem();
        expected.setRequest(null);
        final ItemCreateDto dto = makeItemCreateDtoProxy();
        dto.setRequestId(null);

        final Item actual = mapper.mapToItem(USER_ID, dto);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToItemWhenItemCreateDtoAndUserIdNull() {
        final Item expected = makeTestItem();
        expected.setOwner(null);

        final Item actual = mapper.mapToItem(null, makeItemCreateDtoProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToItemWhenItemCreateDtoAndItemCreatDtoNull() {
        final Item expected = makeTestItem();
        expected.setName(null);
        expected.setDescription(null);
        expected.setAvailable(null);
        expected.setRequest(null);

        final Item actual = mapper.mapToItem(USER_ID, null);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToItemWhenItemCreateDtoAndUserIdNullAndItemCreateDtoNull() {
        final Item actual = mapper.mapToItem(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToItemWhenItemUpdateDto() {
        final Item expected = makeTestItem();
        expected.setOwner(null);
        expected.setComments(null);
        expected.setRequest(null);

        final Item actual = mapper.mapToItem(makeItemUpdateDtoProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToItemWhenItemUpdateDtoNull() {
        final Item actual = mapper.mapToItem(null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleItem() {
        final ItemRetrieveDto expected = makeTestItemRetrieveDto();

        final ItemRetrieveDto actual = mapper.mapToDto(makeItemProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleItemWithRequestId() {
        final ItemRetrieveDto expected = makeTestItemRetrieveDto();
        expected.setRequestId(7L);
        final Item item = makeItemProxy();
        item.setRequest(new Request());
        item.getRequest().setId(7L);

        final ItemRetrieveDto actual = mapper.mapToDto(item);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleItemNull() {
        final ItemRetrieveDto actual = mapper.mapToDto((Item) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenItemList() {
        final ItemRetrieveDto expected = makeTestItemRetrieveDto();

        final List<ItemRetrieveDto> actual = mapper.mapToDto(List.of(makeItemProxy()));

        assertThat(actual, contains(samePropertyValuesAs(expected)));
    }

    @Test
    void testMapToDtoWhenItemListNull() {
        final List<ItemRetrieveDto> actual = mapper.mapToDto((List<Item>) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleBooking() {
        final ItemBookingRetrieveDto expected = makeTestItemBookingRetrieveDto();

        final ItemBookingRetrieveDto actual = mapper.mapToDto(makeTestBooking());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleBookingAndBookerNull() {
        final ItemBookingRetrieveDto expected = makeTestItemBookingRetrieveDto();
        expected.setBookerId(null);
        final Booking booking = makeTestBooking();
        booking.setBooker(null);

        final ItemBookingRetrieveDto actual = mapper.mapToDto(booking);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleBookingNull() {
        final ItemBookingRetrieveDto actual = mapper.mapToDto((Booking) null);

        assertThat(actual, nullValue());
    }
}