package ru.practicum.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.item.dto.ItemDto;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        ItemDto itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.isAvailable());
        if (item.getOwnerId() != null) {
            itemDto.setOwnerId(item.getOwnerId());
        }
        if (item.getRequestId() != null) {
            itemDto.setRequestId(item.getRequestId());
        };
        return itemDto;
    }

    public static Item toEntity(ItemDto itemDto) {
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

    public static List<ItemDto> toItemDtoList(Iterable<Item> items) {
        List<ItemDto> dtos = new ArrayList<>();
        for (Item item : items) {
            dtos.add(toItemDto(item));
        }
        return dtos;
    }

    public static List<ItemDto> toDto(List<Item> items) {
        return items.stream().map(ItemMapper::toItemDto).toList();
    }
}
