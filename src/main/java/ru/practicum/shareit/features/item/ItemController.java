package ru.practicum.shareit.features.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.item.model.ItemDto;
import ru.practicum.shareit.utility.RequestLogger;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAllOwnerItems(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        RequestLogger.logRequest(RequestMethod.GET, "/items");
        return itemService.getAllOwnerItems(ownerId).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader("X-Sharer-User-Id") Long userId,
                               @PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.GET, "/items/" + id);
        return ItemMapper.toItemDto(itemService.getById(userId, id));
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestHeader("X-Sharer-User-Id") Long userId,
                                 @RequestParam("text") String text) {
        RequestLogger.logRequest(RequestMethod.GET, "/items/search?text=" + text);
        return itemService.getSearch(userId, text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PostMapping
    public ItemDto postUser(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                            @Valid @RequestBody ItemDto itemDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/items");
        return ItemMapper.toItemDto(itemService.create(ownerId, ItemMapper.toItem(itemDto)));
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                             @PathVariable Long id,
                             @RequestBody String json) {
        RequestLogger.logRequest(RequestMethod.PATCH, "/items/" + id);
        return ItemMapper.toItemDto(itemService.patch(ownerId, id, json));
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                               @PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.DELETE, "/items/" + id);
        itemService.deleteById(ownerId, id);
    }
}
