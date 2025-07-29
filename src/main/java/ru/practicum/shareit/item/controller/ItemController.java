package ru.practicum.shareit.item.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/items")
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<List<ItemDto>> findAllByOwner(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        return ResponseEntity.ok(itemService.findAllByOwnerId(ownerId));
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDto> findById(@PathVariable Long itemId) {
        return ResponseEntity.ok(itemService.findById(itemId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ItemDto>> search(@RequestParam("text") String text) {
        return ResponseEntity.ok(itemService.search(text));
    }

    @PostMapping
    public ResponseEntity<ItemDto> create(@Valid @RequestBody ItemDto itemDto,
                                          @RequestHeader("X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(CREATED).body(itemService.create(itemDto, userId));
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<ItemDto> update(@PathVariable Long itemId,
                                          @RequestBody ItemDto itemDto,
                                          @RequestHeader(value = "X-Sharer-User-Id") Long userId) {
        return ResponseEntity.status(OK).body(itemService.update(itemId, itemDto, userId));
    }
}
