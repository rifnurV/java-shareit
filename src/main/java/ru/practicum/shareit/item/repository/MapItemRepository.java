package ru.practicum.shareit.item.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.repository.MapUserRepository;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Slf4j
@Repository
public class MapItemRepository implements ItemRepository {

    private final MapUserRepository mapUserRepository;
    Map<Long, Item> items = new HashMap<Long, Item>();
    AtomicLong itemId = new AtomicLong();

    public MapItemRepository(MapUserRepository mapUserRepository) {
        this.mapUserRepository = mapUserRepository;
    }

    @Override
    public Item findByItemId(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item save(Item item) {
        if (item.getId() == null) {
            item.setId(itemId.incrementAndGet());
            items.put(item.getId(), item);
        } else {
            throw new NotFoundException("Item with id " + item.getId() + " already exists");
        }
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (items.containsKey(itemId)) {
            items.put(itemId, item);
        } else {
            throw new NotFoundException("Item not found");
        }
        return item;
    }

    @Override
    public List<Item> findAllByOwnerId(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwner().getId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            return Collections.emptyList();
        }
        String searchTerm = searchText.toLowerCase();

        return items.values().stream()
                .filter(Objects::nonNull)
                .filter(Item::isAvailable)
                .filter(item -> matchesSearchTerm(item, searchTerm))
                .collect(Collectors.toList());
    }

    private boolean matchesSearchTerm(Item item, String searchTerm) {
        return (item.getName() != null && !item.getName().isEmpty() &&
                item.getName().toLowerCase().contains(searchTerm))
                ||
                (item.getDescription() != null && !item.getDescription().isEmpty() &&
                        item.getDescription().toLowerCase().contains(searchTerm));
    }
}
