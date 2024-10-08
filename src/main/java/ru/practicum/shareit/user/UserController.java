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
import ru.practicum.shareit.common.BaseController;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
class UserController extends BaseController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserRetrieveDto createUser(
            @Valid @RequestBody final UserCreateDto userCreateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, userCreateDto);
        final User user = mapper.mapToUser(userCreateDto);
        final UserRetrieveDto dto = mapper.mapToDto(userService.createUser(user));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    public UserRetrieveDto getUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final UserRetrieveDto dto = mapper.mapToDto(userService.getUser(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public List<UserRetrieveDto> getUsers(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<UserRetrieveDto> dtos = mapper.mapToDto(userService.getAllUsers());
        logResponse(request, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public UserRetrieveDto updateUser(
            @PathVariable final long id,
            @RequestBody final UserUpdateDto userUpdateDto,
            final HttpServletRequest request
    ) {
        logRequest(request, userUpdateDto);
        final User user = mapper.mapToUser(userUpdateDto);
        final UserRetrieveDto dto = mapper.mapToDto(userService.updateUser(id, user));
        logResponse(request, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        userService.deleteUser(id);
        logResponse(request);
    }
}
