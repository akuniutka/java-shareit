package ru.practicum.shareit.request;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Objects;

final class ItemRequestUtils {

    private ItemRequestUtils() {
    }

    static ItemRequest makeTestItemRequest() {
        final ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(null);
        itemRequest.setRequester(new User());
        itemRequest.getRequester().setId(42L);
        itemRequest.setDescription("Need the thing");
        itemRequest.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        itemRequest.setItems(new HashSet<>());
        return itemRequest;
    }

    static ItemRequestRetrieveDto makeTestItemRequestRetrieveDto() {
        final ItemRequestRetrieveDto dto = new ItemRequestRetrieveDto();
        dto.setId(7L);
        dto.setDescription("Need the thing");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        dto.setItems(new HashSet<>());
        return dto;
    }

    static ItemRequestItemRetrieveDto makeTestItemRequestItemRetrieveDto() {
        final ItemRequestItemRetrieveDto dto = new ItemRequestItemRetrieveDto();
        dto.setId(13L);
        dto.setOwnerId(42L);
        dto.setName("The next big thing");
        dto.setDescription("This thing is ever stranger");
        dto.setAvailable(false);
        dto.setRequestId(7L);
        return dto;
    }

    static Item makeTestItem() {
        final Item item = new Item();
        item.setId(13L);
        item.setOwner(new User());
        item.getOwner().setId(42L);
        item.setName("The next big thing");
        item.setDescription("This thing is ever stranger");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(new ItemRequest());
        item.getRequest().setId(7L);
        return item;
    }

    static ItemRequest makeItemRequestProxy() {
        final ItemRequest itemRequest = new ItemRequestProxy();
        itemRequest.setId(7L);
        itemRequest.setRequester(new User());
        itemRequest.getRequester().setId(42L);
        itemRequest.setDescription("Need the thing");
        itemRequest.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        itemRequest.setItems(new HashSet<>());
        return itemRequest;
    }

    static ItemRequestCreateDto makeItemRequestCreateDtoProxy() {
        final ItemRequestCreateDto dto = new ItemRequestCreateDtoProxy();
        dto.setDescription("Need the thing");
        return dto;
    }

    static ItemRequestRetrieveDto makeItemRequestRetrieveDtoProxy() {
        final ItemRequestRetrieveDto dto = new ItemRequestRetrieveDtoProxy();
        dto.setId(7L);
        dto.setDescription("Need the thing");
        dto.setCreated(LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1));
        dto.setItems(new HashSet<>());
        return dto;
    }

    static Matcher<ItemRequestRetrieveDto> samePropertyValuesAs(final ItemRequestRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemRequestRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemRequestRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getCreated(), actual.getCreated())
                        && Objects.equals(expected.getItems(), actual.getItems());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    static Matcher<ItemRequestItemRetrieveDto> samePropertyValuesAs(final ItemRequestItemRetrieveDto dto) {
        return new TypeSafeMatcher<>() {

            private final ItemRequestItemRetrieveDto expected = dto;

            @Override
            protected boolean matchesSafely(final ItemRequestItemRetrieveDto actual) {
                return Objects.nonNull(expected) && Objects.nonNull(actual)
                        && Objects.equals(expected.getId(), actual.getId())
                        && Objects.equals(expected.getOwnerId(), actual.getOwnerId())
                        && Objects.equals(expected.getName(), actual.getName())
                        && Objects.equals(expected.getDescription(), actual.getDescription())
                        && Objects.equals(expected.getAvailable(), actual.getAvailable())
                        && Objects.equals(expected.getRequestId(), actual.getRequestId());
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }
}
