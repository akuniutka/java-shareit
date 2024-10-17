package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
interface UserMapper {

    User mapToUser(UserCreateDto dto);

    UserPatch mapToPatch(Long userId, UserUpdateDto dto);

    UserRetrieveDto mapToDto(User user);

    List<UserRetrieveDto> mapToDto(List<User> users);
}
