package ru.practicum.shareit.features.item;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.features.item.model.*;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAllOwnerItems(Long ownerId, Pageable pageable);

    Item getItemById(Long itemId);

    ItemDto getItemDtoById(Long userId, Long itemId);

    ItemShortDto getItemShortById(Long itemId);

    List<ItemForRequestDto> getItemsForRequest(Long requestId);

    List<ItemDto> getSearch(Long userId, String text, Pageable pageable);

    ItemDto create(Long ownerId, ItemDto itemDto);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentDto);

    ItemDto patch(Long ownerId, Long itemId, String json);

    void deleteById(Long ownerId, Long itemId);
}