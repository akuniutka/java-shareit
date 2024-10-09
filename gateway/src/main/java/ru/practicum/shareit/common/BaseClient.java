package ru.practicum.shareit.common;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

public abstract class BaseClient {

    protected final RestTemplate rest;

    protected BaseClient(final RestTemplate rest) {
        this.rest = rest;
    }

    protected Object get(final String path) {
        return get(path, null, null);
    }

    protected Object get(final String path, final Long userId) {
        return get(path, userId, null);
    }

    protected Object get(final String path, final Long userId, final Map<String, Object> parameters) {
        return exchange(HttpMethod.GET, path, userId, parameters, null);
    }

    protected <T> Object post(final String path, final T body) {
        return post(path, null, null, body);
    }

    protected <T> Object post(final String path, final Long userId, final T body) {
        return post(path, userId, null, body);
    }

    protected <T> Object post(final String path, final Long userId, final Map<String, Object> parameters, final T body
    ) {
        return exchange(HttpMethod.POST, path, userId, parameters, body);
    }

    protected <T> Object patch(final String path, final T body) {
        return patch(path, null, null, body);
    }

    protected <T> Object patch(final String path, final Long userId, final T body) {
        return patch(path, userId, null, body);
    }

    protected Object patch(final String path, final Long userId, final Map<String, Object> parameters) {
        return patch(path, userId, parameters, null);
    }

    protected <T> Object patch(final String path, final Long userId, final Map<String, Object> parameters, final T body
    ) {
        return exchange(HttpMethod.PATCH, path, userId, parameters, body);
    }

    protected Object delete(final String path) {
        return delete(path, null, null);
    }

    protected Object delete(final String path, final Long userId) {
        return delete(path, userId, null);
    }

    protected Object delete(final String path, final Long userId,
            final Map<String, Object> parameters
    ) {
        return exchange(HttpMethod.DELETE, path, userId, parameters, null);
    }

    private <T> Object exchange(final HttpMethod method, final String path, final Long userId,
            final Map<String, Object> parameters, final T body
    ) {
        final HttpEntity<T> request = new HttpEntity<>(body, headers(userId, body != null));
        if (parameters != null) {
            return rest.exchange(path, method, request, Object.class, parameters).getBody();
        } else {
            return rest.exchange(path, method, request, Object.class).getBody();
        }
    }

    private HttpHeaders headers(final Long userId, final boolean hasBody) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        if (hasBody) {
            headers.setContentType(MediaType.APPLICATION_JSON);
        }
        if (userId != null) {
            headers.add("X-Sharer-User-Id", String.valueOf(userId));
        }
        return headers;
    }
}
