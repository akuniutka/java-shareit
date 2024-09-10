package ru.practicum.shareit.user;

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
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserMapper mapper;

    @PostMapping
    public UserDto createUser(@RequestBody final NewUserDto newUserDto) {
        log.info("Received POST at /users: {}", newUserDto);
        final User user = mapper.mapToUser(newUserDto);
        final UserDto dto = mapper.mapToDto(userService.createUser(user));
        log.info("Responded to POST /users: {}", dto);
        return dto;
    }

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable final long id) {
        log.info("Received GET at /users/{}", id);
        final UserDto dto = mapper.mapToDto(userService.getUser(id));
        log.info("Responded to GET /users/{}: {}", id, dto);
        return dto;
    }

    @GetMapping
    public List<UserDto> getUsers() {
        log.info("Received GET at /users");
        final List<UserDto> dtos = mapper.mapToDto(userService.getAllUsers());
        log.info("Responded to GET /users: {}", dtos);
        return dtos;
    }

    @PatchMapping("/{id}")
    public UserDto updateUser(@PathVariable final long id, @RequestBody final UpdateUserDto updateUserDto) {
        log.info("Received PATCH at /users/{}: {}", id, updateUserDto);
        final User user = mapper.mapToUser(updateUserDto);
        final UserDto dto = mapper.mapToDto(userService.updateUser(id, user));
        log.info("Responded to PATCH /users/{}: {}", id, dto);
        return dto;
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable final long id) {
        log.info("Received DELETE at /users/{}", id);
        userService.deleteUser(id);
        log.info("Responded to DELETE users/{} with no body", id);
    }
}
