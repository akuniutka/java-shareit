package ru.practicum.shareit.item;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
class ItemController extends HttpRequestResponseLogger {

    private final ItemClient client;

    @PostMapping
    public Object createItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody @Valid final ItemCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.createItem(userId, dto);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/{id}")
    public Object getItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getItem(userId, id);
        logResponse(request, response);
        return response;
    }

    @GetMapping
    public Object getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getItems(userId);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/search")
    public Object getItems(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam final String text,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getItems(userId, text);
        logResponse(request, response);
        return response;
    }

    @PostMapping("/{id}/comment")
    public Object addComment(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestBody @Valid final CommentCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.addComment(userId, id, dto);
        logResponse(request, response);
        return response;
    }

    @PatchMapping("/{id}")
    public Object updateItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestBody @Valid final ItemUpdateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.updateItem(userId, id, dto);
        logResponse(request, response);
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteItem(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        client.deleteItem(userId, id);
        logResponse(request);
    }
}
