package ru.practicum.comment;

import ru.practicum.comment.dto.CommentDto;

import java.util.List;

public interface CommentService {

    List<CommentDto> getAll();

    CommentDto get(Long id);

    CommentDto add(CommentDto comment);

    CommentDto update(CommentDto comment);

    void delete(Long id);

    List<CommentDto> findByItemId(Long itemId);

    List<CommentDto> findByItemId(List<Long> itemIds);

    List<CommentDto> findByAuthorId(Long authorId);
}