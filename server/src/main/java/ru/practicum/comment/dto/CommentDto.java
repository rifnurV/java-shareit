package ru.practicum.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class CommentDto {
    private Long id;
    @NotEmpty
    private String text;
    @Positive
    private Long itemId;
    @Positive
    private Long authorId;
    private LocalDateTime created;
    private String authorName;
}
