package ru.practicum.request.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.item.dto.ItemDto;


import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class ItemRequestDto {
    private Long id;
    @NotNull
    @Size(min = 2, max = 1000)
    private String description;
    @NotNull
    @Positive
    private Long requestorId;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDate created;
    private List<ItemDto> items = List.of();
}
