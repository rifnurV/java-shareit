package ru.practicum.shareit.request.dto;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDate;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private LocalDate created;
}
