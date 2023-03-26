package ru.practicum.shareit.features.item;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.features.item.model.Item;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemInMemoryRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private Long newId = 0L;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> getAllOwnerItems(Long ownerId) {
        return items.values()
                .stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public Item create(Long ownerId, Item item) {
        item.setId(++newId);
        item.setOwnerId(ownerId);
        items.put(item.getId(), item);
        log.info("A new item has been added by user id={}: item id={}", ownerId, item.getId());
        return item;
    }

    @Override
    public Item patch(Long ownerId, Long itemId, Item item) {
        items.replace(itemId, item);
        log.info("Item data has been patched by owner id={}: item id={}", ownerId, itemId);
        return item;
    }

    @Override
    public void deleteById(Long ownerId, Long itemId) {
        items.remove(itemId);
        log.info("Item has been deleted by owner id={}: item id={}", ownerId, itemId);
    }
}
