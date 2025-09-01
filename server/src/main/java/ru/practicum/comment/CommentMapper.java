package ru.practicum.comment;

import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@Component
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .itemId(comment.getItemId())
                .authorId(comment.getAuthorId())
                .created(comment.getCreated())
                .build();
        return commentDto;
    }

    public static Comment toComment(CommentDto commentDto) {
        Comment comment = new Comment();
        comment.setId(commentDto.getId());
        comment.setText(commentDto.getText());
        comment.setItemId(commentDto.getItemId());
        comment.setAuthorId(commentDto.getAuthorId());
        comment.setCreated(commentDto.getCreated());
        return comment;
    }

    public static List<CommentDto> toCommentDto(List<Comment> comments) {
        return comments.stream().map(CommentMapper::toCommentDto).toList();
    }
}
