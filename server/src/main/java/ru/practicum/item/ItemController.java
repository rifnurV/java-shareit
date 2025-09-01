package ru.practicum.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import java.util.List;

import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<ItemDto> getByUserId(
            @RequestHeader(value = X_SHARER_USER_ID, required = true) @Positive Long userId) {
        return itemService.getByUserId(userId);
    }

    @GetMapping("/{itemId}")
    @ResponseStatus(HttpStatus.OK)
    public ItemDto get(@PathVariable Long itemId) {
        return itemService.findById(itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestParam("text") String text) {
        return itemService.search(text);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ItemDto create(@Valid @RequestBody ItemDto itemDto,
                          @RequestHeader(X_SHARER_USER_ID) @Positive Long userId) {
        itemDto.setOwnerId(userId);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable Long itemId,
                          @RequestBody ItemDto itemDto,
                          @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        return itemService.update(itemId, itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(X_SHARER_USER_ID) long userId,
                                 @PathVariable long itemId,
                                 @RequestBody CommentDto commentDto) {

        log.info("START endpoint `method:POST /items/{itemId}/comment` (create comment to item by id), item id: {}.", itemId);
        return itemService.addComment(commentDto, itemId, userId);
    }
}
