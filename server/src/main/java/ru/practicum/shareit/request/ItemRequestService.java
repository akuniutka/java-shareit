package ru.practicum.shareit.request;

import java.util.List;

public interface ItemRequestService {

    ItemRequest createItemRequest(ItemRequest itemRequest);

    ItemRequest getItemRequest(long id);

    ItemRequest getItemRequestWithRelations(long id, long userId);

    List<ItemRequest> getOwnRequests(long userId, int from, int size);

    List<ItemRequest> getOthersRequests(long userId, int from, int size);
}
