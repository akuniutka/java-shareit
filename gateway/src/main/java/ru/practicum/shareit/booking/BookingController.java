package ru.practicum.shareit.booking;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
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
@RequestMapping("/bookings")
@RequiredArgsConstructor
class BookingController extends HttpRequestResponseLogger {

    private final BookingClient client;

    @PostMapping
    public Object createBooking(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestBody @Valid final BookingCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.createBooking(userId, dto);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/{id}")
    Object getBooking(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getBooking(userId, id);
        logResponse(request, response);
        return response;
    }

    @GetMapping
    Object getUserBookings(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "ALL") final String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getUserBookings(userId, state, from, size);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/owner")
    Object getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "ALL") final String state,
            @RequestParam(defaultValue = "0") @PositiveOrZero final int from,
            @RequestParam(defaultValue = "10") @Positive final int size,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getOwnerBookings(userId, state, from, size);
        logResponse(request, response);
        return  response;
    }

    @PatchMapping("/{id}")
    Object processBookingRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestParam final boolean approved,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.processBookingRequest(userId, id, approved);
        logResponse(request, response);
        return response;
    }
}
