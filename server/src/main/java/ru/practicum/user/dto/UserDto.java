package ru.practicum.user.dto;

import jakarta.validation.constraints.Email;
import lombok.*;

@Getter
@Setter
@ToString
@Builder(toBuilder = true)
public class UserDto {
    Long id;
    String name;
    @Email
    String email;
}