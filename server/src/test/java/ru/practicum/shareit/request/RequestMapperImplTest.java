package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.Item;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.lessThanOrEqualTo;
import static org.hamcrest.Matchers.nullValue;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.request.RequestUtils.deepEqualTo;
import static ru.practicum.shareit.request.RequestUtils.makeTestItem;
import static ru.practicum.shareit.request.RequestUtils.makeTestItemRetrieveDto;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequest;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestCreateDto;
import static ru.practicum.shareit.request.RequestUtils.makeTestRequestRetrieveDto;

class RequestMapperImplTest {

    private RequestMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new RequestMapperImpl();
    }

    @Test
    void testMapToRequest() {
        final LocalDateTime from = LocalDateTime.now();

        final Request actual = mapper.mapToRequest(USER_ID, makeTestRequestCreateDto());

        final LocalDateTime to = LocalDateTime.now();
        assertThat(actual, deepEqualTo(makeTestRequest()
                .withNoId()
                .withEmptyItems()
                .withCreated(actual.getCreated())));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToRequestWhenUserIdNullAndDtoNull() {
        final Request actual = mapper.mapToRequest(null, null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToRequestWhenUserIdNull() {
        final LocalDateTime from = LocalDateTime.now();

        final Request actual = mapper.mapToRequest(null, makeTestRequestCreateDto());

        final LocalDateTime to = LocalDateTime.now();
        assertThat(actual, deepEqualTo(makeTestRequest()
                .withNoId()
                .withNoRequester()
                .withEmptyItems()
                .withCreated(actual.getCreated())));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToRequestWhenDtoNull() {
        final LocalDateTime from = LocalDateTime.now();

        final Request actual = mapper.mapToRequest(USER_ID, null);

        final LocalDateTime to = LocalDateTime.now();
        assertThat(actual, deepEqualTo(makeTestRequest()
                .withNoId()
                .withNoDescription()
                .withEmptyItems()
                .withCreated(actual.getCreated())));
        assertThat(actual.getCreated(), allOf(greaterThanOrEqualTo(from), lessThanOrEqualTo(to)));
    }

    @Test
    void testMapToDtoWhenSingleRequest() {
        final RequestRetrieveDto actual = mapper.mapToDto(makeTestRequest());

        assertThat(actual, equalTo(makeTestRequestRetrieveDto()));
    }

    @Test
    void testMapToDtoWhenSingleRequestNull() {
        final RequestRetrieveDto actual = mapper.mapToDto((Request) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenSingleRequestAndItemsNull() {
        final RequestRetrieveDto expected = makeTestRequestRetrieveDto().toBuilder()
                .items(null)
                .build();

        final RequestRetrieveDto actual = mapper.mapToDto(makeTestRequest().withNoItems());

        assertThat(actual, equalTo(expected));
    }

    @Test
    void testMapToDtoWhenRequestList() {
        final List<RequestRetrieveDto> actual = mapper.mapToDto(List.of(makeTestRequest()));

        assertThat(actual, contains(makeTestRequestRetrieveDto()));
    }

    @Test
    void testMapToDtoWhenRequestListNull() {
        final List<RequestRetrieveDto> actual = mapper.mapToDto((List<Request>) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenInternalItem() {
        final ItemRetrieveDto actual = mapper.mapToDto(makeTestItem());

        assertThat(actual, equalTo(makeTestItemRetrieveDto()));
    }

    @Test
    void testMapToDtoWhenInternalItemNull() {
        final ItemRetrieveDto actual = mapper.mapToDto((Item) null);

        assertThat(actual, nullValue());
    }

    @Test
    void testMapToDtoWhenInternalItemAndOwnerNull() {
        final ItemRetrieveDto expected = makeTestItemRetrieveDto().toBuilder()
                .ownerId(null)
                .build();
        final Item item = makeTestItem();
        item.setOwner(null);

        final ItemRetrieveDto actual = mapper.mapToDto(item);

        assertThat(actual, equalTo(expected));
    }

    @Test
    void testMapToDtoWhenInternalItemAndRequestNotNull() {
        final ItemRetrieveDto expected = makeTestItemRetrieveDto().toBuilder()
                .requestId(REQUEST_ID)
                .build();
        final Item item = makeTestItem();
        item.setRequest(makeTestRequest());

        final ItemRetrieveDto actual = mapper.mapToDto(item);

        assertThat(actual, equalTo(expected));
    }
}