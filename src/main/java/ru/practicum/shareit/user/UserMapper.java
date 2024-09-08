package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.EntityCopier;
import ru.practicum.shareit.user.dto.NewUserDto;
import ru.practicum.shareit.user.dto.UpdateUserDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

@Mapper
public interface UserMapper extends EntityCopier<User> {

    User mapToUser(NewUserDto dto);

    User mapToUser(UpdateUserDto dto);

    UserDto mapToDto(User user);

    List<UserDto> mapToDto(List<User> users);
}
