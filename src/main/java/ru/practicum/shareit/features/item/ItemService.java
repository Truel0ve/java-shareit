package ru.practicum.shareit.features.item;

import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.item.model.ItemDto;
import ru.practicum.shareit.features.item.model.ItemShortDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAllOwnerItems(Long ownerId);

    Item getItemById(Long itemId);

    ItemDto getItemDtoById(Long userId, Long itemId);

    ItemShortDto getItemShortById(Long itemId);

    List<ItemDto> getSearch(String text);

    ItemDto create(Long ownerId, ItemDto itemDto);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto patch(Long ownerId, Long itemId, String json);

    void deleteById(Long ownerId, Long itemId);
}