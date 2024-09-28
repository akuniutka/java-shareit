package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper
interface CommentMapper {

    Comment mapTpComment(CommentCreateDto dto);

    @Mapping(source = "author.name", target = "authorName")
    CommentRetrieveDto mapToDto(Comment comment);

    Set<CommentRetrieveDto> mapToDto(Set<Comment> comments);
}
