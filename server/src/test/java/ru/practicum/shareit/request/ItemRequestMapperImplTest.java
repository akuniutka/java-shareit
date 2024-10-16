package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestCreateDtoProxy;
import static ru.practicum.shareit.request.ItemRequestUtils.makeItemRequestProxy;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItem;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequest;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequestItemRetrieveDto;
import static ru.practicum.shareit.request.ItemRequestUtils.makeTestItemRequestRetrieveDto;
import static ru.practicum.shareit.request.ItemRequestUtils.samePropertyValuesAs;

class ItemRequestMapperImplTest {

    private ItemRequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ItemRequestMapperImpl();
    }

    @Test
    void testMapToItemRequest() {
        final ItemRequest expected = makeTestItemRequest();
        final LocalDateTime from = LocalDateTime.now();

        final ItemRequest actual = mapper.mapToItemRequest(42L, makeItemRequestCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToItemRequestWhenUserIdNullAndDtoNull() {
        final ItemRequest actual = mapper.mapToItemRequest(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToItemRequestWhenUserIdNull() {
        final ItemRequest expected = makeTestItemRequest();
        expected.setRequester(null);
        final LocalDateTime from = LocalDateTime.now();

        final ItemRequest actual = mapper.mapToItemRequest(null, makeItemRequestCreateDtoProxy());

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToItemRequestWhenDtoNull() {
        final ItemRequest expected = makeTestItemRequest();
        expected.setDescription(null);
        final LocalDateTime from = LocalDateTime.now();

        final ItemRequest actual = mapper.mapToItemRequest(42L, null);

        final LocalDateTime to = LocalDateTime.now();
        expected.setCreated(actual.getCreated());
        assertThat(actual, samePropertyValuesAs(expected));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToDtoWhenSingleItemRequest() {
        final ItemRequestRetrieveDto expected = makeTestItemRequestRetrieveDto();

        final ItemRequestRetrieveDto actual = mapper.mapToDto(makeItemRequestProxy());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleItemRequestNull() {
        final ItemRequestRetrieveDto actual = mapper.mapToDto((ItemRequest) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleItemRequestAndItemsNull() {
        final ItemRequestRetrieveDto expected = makeTestItemRequestRetrieveDto();
        expected.setItems(null);
        final ItemRequest itemRequest = makeItemRequestProxy();
        itemRequest.setItems(null);

        final ItemRequestRetrieveDto actual = mapper.mapToDto(itemRequest);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenSingleItemRequestWithItem() {
        final ItemRequestRetrieveDto expected = makeTestItemRequestRetrieveDto();
        expected.getItems().add(makeTestItemRequestItemRetrieveDto());
        final ItemRequest itemRequest = makeItemRequestProxy();
        itemRequest.getItems().add(makeTestItem());

        final ItemRequestRetrieveDto actual = mapper.mapToDto(itemRequest);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenItemRequestList() {
        final ItemRequestRetrieveDto expected = makeTestItemRequestRetrieveDto();

        final List<ItemRequestRetrieveDto> actual = mapper.mapToDto(List.of(makeItemRequestProxy()));

        assertThat(actual, contains(samePropertyValuesAs(expected)));
    }

    @Test
    void testMapToDtoWhenItemRequestListNull() {
        final List<ItemRequestRetrieveDto> actual = mapper.mapToDto((List<ItemRequest>) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenInternalItem() {
        final ItemRequestItemRetrieveDto expected = makeTestItemRequestItemRetrieveDto();

        final ItemRequestItemRetrieveDto actual = mapper.mapToDto(makeTestItem());

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenInternalItemNull() {
        final ItemRequestItemRetrieveDto actual = mapper.mapToDto((Item) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenInternalItemAndOwnerNull() {
        final ItemRequestItemRetrieveDto expected = makeTestItemRequestItemRetrieveDto();
        expected.setOwnerId(null);
        final Item item = makeTestItem();
        item.setOwner(null);

        final ItemRequestItemRetrieveDto actual = mapper.mapToDto(item);

        assertThat(actual, samePropertyValuesAs(expected));
    }

    @Test
    void testMapToDtoWhenInternalItemAndRequestNull() {
        final ItemRequestItemRetrieveDto expected = makeTestItemRequestItemRetrieveDto();
        expected.setRequestId(null);
        final Item item = makeTestItem();
        item.setRequest(null);

        final ItemRequestItemRetrieveDto actual = mapper.mapToDto(item);

        assertThat(actual, samePropertyValuesAs(expected));
    }
}