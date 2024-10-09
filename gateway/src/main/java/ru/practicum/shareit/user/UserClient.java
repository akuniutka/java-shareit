package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

import java.util.Objects;

@Service
@Validated
class UserClient extends BaseClient {

    UserClient(@Value("${shareit-server.url}") final String serverUrl, final RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    Object createUser(@Valid final UserCreateDto dto) {
        Objects.requireNonNull(dto, "Cannot create user: is null");
        return post("", dto);
    }

    Object getUser(final long id) {
        return get("/" + id);
    }

    Object getUsers() {
        return get("");
    }

    Object updateUser(final long id, @Valid final UserUpdateDto dto) {
        Objects.requireNonNull(dto, "Cannot update user: is null");
        return patch("/" + id, dto);
    }

    void deleteUser(final long id) {
        delete("/" + id);
    }
}
