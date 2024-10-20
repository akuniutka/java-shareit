package ru.practicum.shareit.request;

import jakarta.validation.Valid;

import java.util.List;

public interface RequestService {

    Request createRequest(@Valid Request request);

    Request getRequest(long id);

    Request getRequestWithRelations(long id, long userId);

    List<Request> getOwnRequests(long userId, int from, int size);

    List<Request> getOthersRequests(long userId, int from, int size);
}
