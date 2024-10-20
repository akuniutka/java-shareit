package ru.practicum.shareit.request;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.time.Month;
import java.util.HashSet;
import java.util.Set;

import static ru.practicum.shareit.common.CommonUtils.ANOTHER_USER_ID;
import static ru.practicum.shareit.common.CommonUtils.ITEM_ID;
import static ru.practicum.shareit.common.CommonUtils.REQUEST_ID;
import static ru.practicum.shareit.common.CommonUtils.USER_ID;

final class RequestUtils {

    private static final LocalDateTime CREATED = LocalDateTime.of(2001, Month.JANUARY, 1, 0, 0, 1);

    private RequestUtils() {
    }

    static RequestProxy makeTestRequest() {
        final RequestProxy request = new RequestProxy();
        request.setId(REQUEST_ID);
        request.setRequester(makeTestUser(USER_ID));
        request.setDescription("Need the thing");
        request.setCreated(CREATED);
        final Item item = makeTestItem();
        item.setRequest(request);
        request.setItems(Set.of(item));
        return request;
    }

    static RequestCreateDto makeTestRequestCreateDto() {
        return new RequestCreateDto("Need the thing");
    }

    static RequestRetrieveDto makeTestRequestRetrieveDto() {
        return RequestRetrieveDto.builder()
                .id(REQUEST_ID)
                .description("Need the thing")
                .created(CREATED)
                .items(Set.of(makeTestItemRetrieveDto().toBuilder()
                        .requestId(REQUEST_ID)
                        .build()))
                .build();
    }

    static Item makeTestItem() {
        final Item item = new Item();
        item.setId(ITEM_ID);
        item.setOwner(makeTestUser(ANOTHER_USER_ID));
        item.setName("The next big thing");
        item.setDescription("This thing is ever stranger");
        item.setAvailable(false);
        item.setLastBooking(null);
        item.setNextBooking(null);
        item.setComments(new HashSet<>());
        item.setRequest(null);
        return item;
    }

    static ItemRetrieveDto makeTestItemRetrieveDto() {
        return ItemRetrieveDto.builder()
                .id(ITEM_ID)
                .ownerId(ANOTHER_USER_ID)
                .name("The next big thing")
                .description("This thing is ever stranger")
                .available(false)
                .requestId(null)
                .build();
    }

    static <T extends Request> Matcher<T> deepEqualTo(final RequestProxy request) {
        return new TypeSafeMatcher<>() {

            private final RequestProxy expected = request;

            @Override
            protected boolean matchesSafely(final T actual) {
                return expected.equals(actual);
            }

            @Override
            public void describeTo(final Description description) {
                description.appendValue(expected);
            }
        };
    }

    private static User makeTestUser(final long userId) {
        final User user = new User();
        user.setId(userId);
        return user;
    }
}
