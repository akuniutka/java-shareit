package ru.practicum.shareit.item;

import jakarta.validation.Valid;

interface CommentService {

    Comment addComment(@Valid Comment comment);
}
