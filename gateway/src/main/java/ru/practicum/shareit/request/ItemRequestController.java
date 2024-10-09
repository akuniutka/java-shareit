package ru.practicum.shareit.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController extends HttpRequestResponseLogger {

    private final ItemRequestClient client;

    @PostMapping
    Object createItemRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody @Valid final ItemRequestCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.createItemRequest(userId, dto);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/{id}")
    Object getItemRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest((request));
        final Object response = client.getItemRequest(userId, id);
        logResponse(request, response);
        return response;
    }

    @GetMapping
    Object getOwnItemRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getOwnItemRequests(userId, from, size);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/all")
    Object getOthersRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getOthersItemRequests(userId, from, size);
        logResponse(request, response);
        return response;
    }
}
