package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.EntityCopier;

import java.util.List;

@Mapper
interface UserMapper extends EntityCopier<User> {

    User mapToUser(UserCreateDto dto);

    User mapToUser(UserUpdateDto dto);

    UserRetrieveDto mapToDto(User user);

    List<UserRetrieveDto> mapToDto(List<User> users);
}
