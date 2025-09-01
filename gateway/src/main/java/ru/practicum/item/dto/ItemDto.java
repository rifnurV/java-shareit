package ru.practicum.item.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.comment.dto.CommentDto;

import java.util.List;

@Getter
@Setter
public class ItemDto {
    private Long id;
    @NotNull
    @Size(min = 2, max = 100)
    private String name;
    @NotNull
    @Size(min = 10, max = 1000)
    private String description;
    @NotNull
    private Boolean available;
    private Long ownerId;
    private Long requestId;

    private List<CommentDto> comments;
    private Long lastBooking;
    private Long nextBooking;
}
