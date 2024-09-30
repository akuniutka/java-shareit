package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.time.LocalDateTime;
import java.util.Set;

@Mapper
interface CommentMapper {

    @Mapping(target = "created", expression = "java(now())")
    Comment mapToComment(CommentCreateDto dto);

    @Mapping(target = "authorName", source = "author.name")
    CommentRetrieveDto mapToDto(Comment comment);

    Set<CommentRetrieveDto> mapToDto(Set<Comment> comments);

    default LocalDateTime now() {
        return LocalDateTime.now();
    }
}
