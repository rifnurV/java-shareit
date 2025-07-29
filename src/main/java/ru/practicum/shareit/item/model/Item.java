package ru.practicum.shareit.item.model;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;

@Getter
@Setter
public class Item {
    Long id;
    @NotNull
    @Size(min = 2, max = 100)
    String name;
    @NotNull
    @Size(min = 10, max = 1000)
    String description;
    Boolean available = true;
    User owner;
    ItemRequest request;

    public Boolean isAvailable() {
        return available;
    }
}
