package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBookingsAndComments;

import java.util.List;

public interface ItemService {
    ItemDtoBookingsAndComments findById(Long ownerId, Long itemId);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    List<ItemDtoBookingsAndComments> findAllByOwnerId(Long ownerId);

    List<ItemDto> search(@RequestParam("text") String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long authorId);
}
