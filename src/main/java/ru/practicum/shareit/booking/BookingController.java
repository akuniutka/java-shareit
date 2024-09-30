package ru.practicum.shareit.booking;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.shareit.common.exception.UnsupportedBookingStateFilterException;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping(path = "/bookings")
@AllArgsConstructor
@Slf4j
class BookingController extends HttpRequestResponseLogger {

    private final BookingService bookingService;
    private final BookingMapper mapper;

    @PostMapping
    public BookingRetrieveDto createBooking(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @Valid @RequestBody final BookingCreateDto bookingCreateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, bookingCreateDto);
        final Booking booking = mapper.mapToBooking(bookingCreateDto);
        final BookingRetrieveDto dto = mapper.mapToDto(bookingService.createBooking(booking, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    public BookingRetrieveDto getBooking(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final BookingRetrieveDto dto = mapper.mapToDto(bookingService.getBooking(id, userId));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public List<BookingRetrieveDto> getUserBookings(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "ALL") final String state,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final BookingStatusFilter filter = convertToFilter(state);
        final List<BookingRetrieveDto> dtos = mapper.mapToDto(bookingService.getUserBookings(userId, filter));
        logResponse(request, dtos);
        return dtos;
    }

    @GetMapping("/owner")
    public List<BookingRetrieveDto> getOwnerBookings(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @RequestParam(defaultValue = "ALL") final String state,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final BookingStatusFilter filter = convertToFilter(state);
        final List<BookingRetrieveDto> dtos = mapper.mapToDto(bookingService.getOwnerBookings(userId, filter));
        logResponse(request, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public BookingRetrieveDto processBookingRequest(
            @RequestHeader("X-Sharer-User-Id") final long userId,
            @PathVariable final long id,
            @RequestParam final boolean approved,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final BookingRetrieveDto dto = mapper.mapToDto(bookingService.applyBookingVerdict(id, approved, userId));
        logResponse(request, dto);
        return dto;
    }

    // TODO: Delete after sprint #16. Just to pass Postman test with hardcoded error message.
    private BookingStatusFilter convertToFilter(final String state) {
        return Arrays.stream(BookingStatusFilter.values())
                .filter(value -> Objects.equals(value.name(), state))
                .findAny()
                .orElseThrow(
                        () -> new UnsupportedBookingStateFilterException(state)
                );
    }
}
