package ru.practicum.shareit.user;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public Object createUser(
            @RequestBody @Valid final UserCreateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request, dto);
        final Object response = client.createUser(dto);
        logResponse(request, response);
        return response;
    }

    @GetMapping("/{id}")
    public Object getUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getUser(id);
        logResponse(request, response);
        return response;
    }

    @GetMapping
    public Object getUsers(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.getUsers();
        logResponse(request, response);
        return response;
    }

    @PatchMapping("/{id}")
    public Object updateUser(
            @PathVariable final long id,
            @RequestBody @Valid final UserUpdateDto dto,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final Object response = client.updateUser(id, dto);
        logResponse(request, response);
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
