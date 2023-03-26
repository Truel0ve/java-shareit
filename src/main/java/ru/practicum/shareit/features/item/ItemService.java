package ru.practicum.shareit.features.item;

import ru.practicum.shareit.features.item.model.Item;

import java.util.List;

public interface ItemService {
    List<Item> getAllOwnerItems(Long ownerId);

    Item getById(Long userId, Long itemId);

    List<Item> getSearch(Long userId, String text);

    Item create(Long ownerId, Item item);

    Item patch(Long ownerId, Long itemId, String json);

    void deleteById(Long ownerId, Long itemId);
}