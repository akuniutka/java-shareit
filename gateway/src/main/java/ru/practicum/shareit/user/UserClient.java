package ru.practicum.shareit.user;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

@Service
class UserClient extends BaseClient {

    UserClient(@Value("${shareit-server.url}") final String serverUrl, final RestTemplateBuilder restTemplateBuilder) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/users"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    ResponseEntity<Object> createUser(final UserCreateDto dto) {
        return post("", dto);
    }

    ResponseEntity<Object> getUser(final long id) {
        return get("/" + id);
    }

    ResponseEntity<Object> getUsers() {
        return get("");
    }

    ResponseEntity<Object> updateUser(final long id, final UserUpdateDto dto) {
        return patch("/" + id, dto);
    }

    void deleteUser(final long id) {
        delete("/" + id);
    }
}
