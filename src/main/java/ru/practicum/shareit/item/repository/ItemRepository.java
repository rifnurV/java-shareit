package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {
    Item findByItemId(Long id);

    Item save(Item item);

    Item update(Long id, Item item);

    List<Item> findAllByOwnerId(Long ownerId);

    List<Item> search(String text);
}
