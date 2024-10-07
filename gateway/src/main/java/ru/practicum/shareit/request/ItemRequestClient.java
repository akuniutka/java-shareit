package ru.practicum.shareit.request;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

@Service
class ItemRequestClient extends BaseClient {

    ItemRequestClient(
            @Value("${shareit-server.url}") final String serverUrl,
            final RestTemplateBuilder restTemplateBuilder
    ) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    ResponseEntity<Object> createItemRequest(final long userId, final ItemRequestCreateDto dto) {
        return post("", userId, dto);
    }

    ResponseEntity<Object> getItemRequest(final long userId, final long id) {
        return get("/" + id, userId);
    }

    ResponseEntity<Object> getOwnItemRequests(final long userId) {
        return get("", userId);
    }

    ResponseEntity<Object> getOthersItemRequests(final long userId) {
        return get("/all", userId);
    }
}
