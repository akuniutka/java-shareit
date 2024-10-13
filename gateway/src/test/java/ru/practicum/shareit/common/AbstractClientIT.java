package ru.practicum.shareit.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.ResponseActions;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

public abstract class AbstractClientIT {

    protected static final String HEADER = "X-Sharer-User-Id";

    @Autowired
    protected MockRestServiceServer mockServer;

    @Value("${shareit-server.url}")
    protected String baseUrl;

    protected String basePath = "";

    protected ResponseActions expectPost(final String jsonBody) {
        return expectPost("", jsonBody);
    }

    protected ResponseActions expectPost(long userId, final String jsonBody) {
        return expectPost("", userId, jsonBody);
    }

    protected ResponseActions expectPost(final String path, final String jsonBody) {
        return expectBase(path)
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonBody, true));
    }

    protected ResponseActions expectPost(final String path, final long userId, final String jsonBody) {
        return expectPost(path, jsonBody)
                .andExpect(header(HEADER, String.valueOf(userId)));
    }

    protected ResponseActions expectGet() {
        return expectGet("");
    }

    protected ResponseActions expectGet(long userId) {
        return expectGet("", userId);
    }

    protected ResponseActions expectGet(final String path) {
        return expectBase(path)
                .andExpect(method(HttpMethod.GET));
    }

    protected ResponseActions expectGet(final String path, long userId) {
        return expectGet(path)
                .andExpect(header(HEADER, String.valueOf(userId)));
    }

    protected ResponseActions expectPatch(final String path, final String jsonBody) {
        return expectBase(path)
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(jsonBody, true));
    }

    protected ResponseActions expectPatch(final String path, final long userId) {
        return expectBase(path)
                .andExpect(header(HEADER, String.valueOf(userId)));
    }

    protected ResponseActions expectPatch(final String path, final long userId, final String jsonBody) {
        return expectPatch(path, jsonBody)
                .andExpect(header(HEADER, String.valueOf(userId)));
    }

    protected ResponseActions expectDelete(final String path) {
        return expectBase(path)
                .andExpect(method(HttpMethod.DELETE));
    }

    protected ResponseActions expectDelete(final String path, final long userId) {
        return expectDelete(path)
                .andExpect(header(HEADER, String.valueOf(userId)));
    }

    private ResponseActions expectBase(final String path) {
        return mockServer.expect(ExpectedCount.once(), requestTo(baseUrl + basePath + path))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE));
    }
}
