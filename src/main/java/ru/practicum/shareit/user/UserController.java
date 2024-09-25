package ru.practicum.shareit.user;

import jakarta.servlet.http.HttpServletRequest;
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
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController extends BaseController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserDto createUser(
            @RequestBody final NewUserDto newUserDto,
            final HttpServletRequest request
    ) {
        logRequest(request, newUserDto);
        final User user = mapper.mapToUser(newUserDto);
        final UserDto dto = mapper.mapToDto(userService.createUser(user));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping("/{id}")
    public UserDto getUser(
            @PathVariable final long id,
            final HttpServletRequest request
    ) {
        logRequest(request);
        final UserDto dto = mapper.mapToDto(userService.getUser(id));
        logResponse(request, dto);
        return dto;
    }

    @GetMapping
    public List<UserDto> getUsers(
            final HttpServletRequest request
    ) {
        logRequest(request);
        final List<UserDto> dtos = mapper.mapToDto(userService.getAllUsers());
        logResponse(request, dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(
            @PathVariable final long id,
            @RequestBody final UpdateUserDto updateUserDto,
            final HttpServletRequest request
    ) {
        logRequest(request, updateUserDto);
        final User user = mapper.mapToUser(updateUserDto);
        final UserDto dto = mapper.mapToDto(userService.updateUser(id, user));
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
