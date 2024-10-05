package ru.practicum.shareit.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.common.HttpRequestResponseLogger;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Slf4j
class UserController extends HttpRequestResponseLogger {

    private final UserClient client;

    @PostMapping
    public ResponseEntity<Object> createUser(
            @RequestBody @Valid final UserCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final ResponseEntity<Object> response = client.createUser(dto);
        logResponse(request, response.getBody());
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ResponseEntity<Object> response = client.getUser(id);
        logResponse(request, response.getBody());
        return response;
    }

    @GetMapping
    public ResponseEntity<Object> getUsers(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ResponseEntity<Object> response = client.getUsers();
        logResponse(request, response.getBody());
        return response;
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(
            @PathVariable final long id,
            @RequestBody @Valid final UserUpdateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final ResponseEntity<Object> response = client.updateUser(id, dto);
        logResponse(request, response.getBody());
        return response;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        client.deleteUser(id);
        logResponse(request);
    }
}
