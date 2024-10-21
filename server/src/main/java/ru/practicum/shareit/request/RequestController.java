package ru.practicum.shareit.request;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
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
class RequestController extends HttpRequestResponseLogger {

    private final RequestService requestService;
    private final RequestMapper mapper;

    @PostMapping
    RequestRetrieveDto createRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody @Valid final RequestCreateDto requestCreateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, requestCreateDto);
        final Request itemRequest = mapper.mapToRequest(userId, requestCreateDto);
        final RequestRetrieveDto dto = mapper.mapToDto(requestService.createRequest(itemRequest));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    RequestRetrieveDto getRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final RequestRetrieveDto dto = mapper.mapToDto(requestService.getRequestWithRelations(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    List<RequestRetrieveDto> getOwnRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<RequestRetrieveDto> dtos = mapper.mapToDto(requestService.getOwnRequests(userId, from, size));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/all")
    List<RequestRetrieveDto> getOthersRequests(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<RequestRetrieveDto> dtos = mapper.mapToDto(requestService.getOthersRequests(userId, from, size));
        logResponse(request, dtos);
        return dtos;
    }
}
