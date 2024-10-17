package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;

@Mapper
interface CommentMapper {

    @Mapping(target = "item.id", source = "itemId")
    @Mapping(target = "author.id", source = "userId")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    Comment mapToComment(Long userId, Long itemId, CommentCreateDto dto);

    @Mapping(target = "authorName", source = "author.name")
    CommentRetrieveDto mapToDto(Comment comment);

    Set<CommentRetrieveDto> mapToDto(Set<Comment> comments);
}
