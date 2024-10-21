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
import java.util.Objects;

@Service
@Validated
class RequestClient extends BaseClient {

    RequestClient(
            @Value("${shareit-server.url}") final String serverUrl,
            final RestTemplateBuilder restTemplateBuilder
    ) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/requests"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    Object createRequest(final long userId, @Valid final RequestCreateDto dto) {
        Objects.requireNonNull(dto, "Cannot create item request: is null");
        return post("", userId, dto);
    }

    Object getRequest(final long userId, final long id) {
        return get("/" + id, userId);
    }

    Object getOwnRequests(final long userId, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("?from={from}&size={size}", userId, parameters);
    }

    Object getOthersRequests(final long userId, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size);
        return get("/all?from={from}&size={size}", userId, parameters);
    }
}
