package ru.practicum.item;

import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto findById(Long id);

    List<ItemDto> findById(List<Long> ids);

    ItemDto create(ItemDto itemDto, Long userId);

    ItemDto update(Long itemId, ItemDto itemDto, Long userId);

    List<ItemDto> search(@RequestParam("text") String text);

    CommentDto addComment(CommentDto commentDto, Long itemId, Long authorId);

    List<ItemDto> getByRequestIds(List<Long> requestIds);

    List<ItemDto> getByUserId(Long userId);

    boolean checkIdExist(Long id);

    boolean isItemAvailable(Long itemId);
}
