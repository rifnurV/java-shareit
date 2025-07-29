package ru.practicum.shareit.user.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class User {
    Long id;
    @NotNull
    @Size(min = 3, max = 100)
    String name;
    @Email
    @NotNull
    String email;
}
