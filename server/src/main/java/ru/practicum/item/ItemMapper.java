package ru.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ItemMapper {
    public ItemDto toItemDto(Item item) {
        ItemDto itemDto = ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.isAvailable())
                .build();

        if (item.getOwnerId() != null) {
            itemDto.setOwnerId(item.getOwnerId());
        }
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        }
        ;
        return itemDto;
    }

    public Item toEntity(ItemDto itemDto) {
        Item item = new Item();
        item.setId(itemDto.getId());
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.getAvailable());
        if (itemDto.getOwnerId() != null) {
            item.setOwnerId(itemDto.getOwnerId());
        }
        if (itemDto.getRequestId() != null) {
            item.setRequestId(itemDto.getRequestId());
        }
        return item;
    }

    public List<ItemDto> toItemDtoList(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(this::toItemDto).toList();
    }
}
