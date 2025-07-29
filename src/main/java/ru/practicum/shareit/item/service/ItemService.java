package ru.practicum.shareit.item.service;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto findById(Long itemId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    List<ItemDto> findAllByOwnerId(Long ownerId);

    List<ItemDto> search(@RequestParam("text") String text);
}
