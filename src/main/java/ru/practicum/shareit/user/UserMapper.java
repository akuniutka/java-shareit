package ru.practicum.shareit.user;

import org.mapstruct.Mapper;

import java.util.List;

@Mapper
interface UserMapper {

    User mapToUser(UserCreateDto dto);

    User mapToUser(UserUpdateDto dto);

    UserRetrieveDto mapToDto(User user);

    List<UserRetrieveDto> mapToDto(List<User> users);
}
