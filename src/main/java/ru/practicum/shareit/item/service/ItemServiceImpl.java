package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.MapUserRepository;
import ru.practicum.shareit.user.model.User;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;
    private final MapUserRepository mapUserRepository;

    @Override
    public ItemDto findById(Long itemId) {
        Item item = itemRepository.findByItemId(itemId);
        if (item == null) {
            throw new NotFoundException("Item not found");
        }
        return itemMapper.toItemDto(item);
    }

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        User owner = mapUserRepository.findByUserId(ownerId);
        if (owner == null) {
            throw new NotFoundException(String.format("User with id %s not found", ownerId));
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new NotFoundException("Name is required");
        }
        if (itemDto.getDescription() == null || itemDto.getDescription().isBlank()) {
            throw new NotFoundException("Description is required");
        }

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("Available item is required");
        }

        Item item = itemMapper.toEntity(itemDto);
        item.setOwner(owner);

        Item newItem = itemRepository.save(item);
        return itemMapper.toItemDto(newItem);
    }

    @Override
    public ItemDto update(Long itemId, ItemDto itemDto, Long ownerId) {
        Item itemOld = itemRepository.findByItemId(itemId);
        if (itemOld == null) {
            throw new NotFoundException(String.format("Item with id %s not found", itemId));
        }
        if (!itemOld.getOwner().getId().equals(ownerId)) {
            throw new NotFoundException("Owner id mismatch");
        }
        if (itemDto.getName() != null) {
            itemOld.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            itemOld.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            itemOld.setAvailable(itemDto.getAvailable());
        }
        Item updatedItem = itemRepository.update(itemId, itemOld);
        return itemMapper.toItemDto(updatedItem);
    }

    @Override
    public List<ItemDto> findAllByOwnerId(Long ownerId) {
        List<Item> item = itemRepository.findAllByOwnerId(ownerId);

        return item.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text == null || text.isBlank()) {
            return Collections.EMPTY_LIST;
        }
        List<Item> item = itemRepository.search(text);
        return item.stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
