package ru.practicum.item.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
@Builder
public class ItemDto {
    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @Positive
    private Long ownerId;
    @Positive
    private Long requestId;
    private List<CommentDto> comments;
    private Long lastBooking;
    private Long nextBooking;
}
