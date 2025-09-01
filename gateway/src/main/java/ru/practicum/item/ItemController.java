package ru.practicum.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.item.dto.ItemDto;

import static ru.practicum.constant.Constant.X_SHARER_USER_ID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> findAllByOwner(@RequestHeader(X_SHARER_USER_ID) Long ownerId) {
        return itemClient.findAllByOwnerId(ownerId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> findById(@RequestHeader(X_SHARER_USER_ID) Long ownerId,
                                           @PathVariable Long itemId) {
        return itemClient.findById(ownerId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text) {
        return itemClient.search(text);
    }

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody ItemDto itemDto,
                                         @RequestHeader(X_SHARER_USER_ID) Long userId) {
        itemDto.setOwnerId(userId);
        Validate.itemDto(itemDto);
        return itemClient.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable Long itemId,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader(value = X_SHARER_USER_ID) Long userId) {
        return itemClient.update(itemId, itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestBody @Valid CommentDto commentDto,
                                             @PathVariable Long itemId, @RequestHeader(X_SHARER_USER_ID) Long authorId) {
        log.info("Получен запрос POST /items");
        return itemClient.addComment(commentDto, itemId, authorId);
    }
}
