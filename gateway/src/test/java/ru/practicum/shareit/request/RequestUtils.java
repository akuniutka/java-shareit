package ru.practicum.shareit.request;

final class RequestUtils {

    private RequestUtils() {
    }

    static RequestCreateDto makeTestRequestCreateDto() {
        return new RequestCreateDto("Need the thing");
    }
}
