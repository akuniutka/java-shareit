package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

import java.util.Map;

@Service
@Validated
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

    Object createItemRequest(final long userId, @Valid final ItemRequestCreateDto dto) {
        return post("", userId, dto);
    }

    Object getItemRequest(final long userId, final long id) {
        return get("/" + id, userId);
    }

    Object getOwnItemRequests(final long userId, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    Object getOthersItemRequests(final long userId, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
