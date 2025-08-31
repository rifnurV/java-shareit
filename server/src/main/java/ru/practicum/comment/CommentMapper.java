package ru.practicum.comment;

import lombok.experimental.UtilityClass;
import org.springframework.stereotype.Component;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@Component
public class CommentMapper {

    public static CommentDto toCommentDto(Comment comment) {
        CommentDto commentDto = new CommentDto();
        commentDto.setId(comment.getId());
        commentDto.setText(comment.getText());
        commentDto.setItemId(comment.getItemId());
        commentDto.setAuthorId(comment.getAuthorId());
        commentDto.setCreated(comment.getCreated());
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
