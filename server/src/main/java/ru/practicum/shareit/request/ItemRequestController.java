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

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
class ItemRequestController extends HttpRequestResponseLogger {

    private final ItemRequestService itemRequestService;
    private final ItemRequestMapper mapper;

    @PostMapping
    ItemRequestRetrieveDto createItemRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody @Valid final ItemRequestCreateDto itemRequestCreateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, itemRequestCreateDto);
        final ItemRequest itemRequest = mapper.mapToItemRequest(userId, itemRequestCreateDto);
        final ItemRequestRetrieveDto dto = mapper.mapToDto(itemRequestService.createItemRequest(itemRequest));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    ItemRequestRetrieveDto getItemRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ItemRequestRetrieveDto dto = mapper.mapToDto(itemRequestService.getItemRequestWithRelations(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    List<ItemRequestRetrieveDto> getOwnItemRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemRequestRetrieveDto> dtos = mapper.mapToDto(itemRequestService.getOwnRequests(userId, from,
                size));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/all")
    List<ItemRequestRetrieveDto> getOthersItemRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<ItemRequestRetrieveDto> dtos = mapper.mapToDto(itemRequestService.getOthersRequests(userId, from,
                size));
        logResponse(request, dtos);
        return dtos;
    }
}
