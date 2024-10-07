package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

import java.util.Map;

@Service
class ItemClient extends BaseClient {

    ItemClient(@Value("${shareit-server.url}") final String serverUrl, final RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/items"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    ResponseEntity<Object> createItem(final long userId, final ItemCreateDto dto) {
        return post("", userId, dto);
    }

    ResponseEntity<Object> getItem(final long userId, final long id) {
        return get("/" + id, userId);
    }

    ResponseEntity<Object> getItems(final long userId) {
        return get("", userId);
    }

    ResponseEntity<Object> getItems(final long userId, final String text) {
        return get("/search?text={text}", userId, Map.of("text", text));
    }

    ResponseEntity<Object> addComment(final long userId, final long id, final CommentCreateDto dto) {
        return post("/" + id + "/comment", userId, dto);
    }

    ResponseEntity<Object> updateItem(final long userId, final long id, final ItemUpdateDto dto) {
        return patch("/" + id, userId, dto);
    }

    void deleteItem(final long userId, final long id) {
        delete("/" + id, userId);
    }
}
