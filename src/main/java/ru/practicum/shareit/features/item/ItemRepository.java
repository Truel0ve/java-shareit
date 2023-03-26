package ru.practicum.shareit.features.item;

import ru.practicum.shareit.features.item.model.Item;

import java.util.List;

public interface ItemRepository {
    List<Item> getAll();

    List<Item> getAllOwnerItems(Long ownerId);

    Item getById(Long itemId);

    Item create(Long ownerId, Item item);

    Item patch(Long ownerId, Long itemId, Item item);

    void deleteById(Long ownerId, Long itemId);
}