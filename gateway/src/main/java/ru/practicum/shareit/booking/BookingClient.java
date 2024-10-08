package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.common.BaseClient;

import java.util.Map;
import java.util.Objects;

@Service
@Validated
class BookingClient extends BaseClient {

    BookingClient(
            @Value("${shareit-server.url}") final String serverUrl,
            final RestTemplateBuilder restTemplateBuilder
    ) {
        super(restTemplateBuilder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + "/bookings"))
                .requestFactory(() -> new HttpComponentsClientHttpRequestFactory())
                .build());
    }

    ResponseEntity<Object> createBooking(final long userId, @Valid final BookingCreateDto dto) {
        Objects.requireNonNull(dto, "Cannot create booking: is null");
        return post("", userId, dto);
    }

    ResponseEntity<Object> getBooking(final long userId, final long id) {
        return get("/" + id, userId);
    }

    ResponseEntity<Object> getUserBookings(final long userId, final String state, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("?state={state}&from={from}&size={size}", userId, parameters);
    }

    ResponseEntity<Object> getOwnerBookings(final long userId, final String state, final int from, final int size) {
        final Map<String, Object> parameters = Map.of(
                "state", state,
                "from", from,
                "size", size);
        return get("/owner?state={state}&from={from}&size={size}", userId, parameters);
    }

    ResponseEntity<Object> processBookingRequest(final long userId, final long id, final boolean approved) {
        return patch("/" + id + "?approved={approved}", userId, Map.of("approved", approved));
    }
}
