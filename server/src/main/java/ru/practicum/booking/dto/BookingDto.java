package ru.practicum.booking.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.booking.BookingStatus;
import ru.practicum.item.dto.ItemDto;
import ru.practicum.user.dto.UserDto;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
public class BookingDto {
    private Long id;
    @NotNull
    @Future
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
    @NotNull
    @Positive
    private Long itemId;
    @Positive
    private Long bookerId;
    private BookingStatus status;
    @Valid
    private ItemDto item;
    @Valid
    private UserDto booker;
}
