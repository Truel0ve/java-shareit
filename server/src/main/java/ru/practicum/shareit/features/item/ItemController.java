package ru.practicum.shareit.features.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.ItemDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllOwnerItems(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestParam(defaultValue = "0", required = false) Integer from,
                                          @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.getAllOwnerItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @PathVariable Long id) {
        return itemService.getItemDtoById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @RequestParam("text") String text,
                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                   @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemService.getSearch(userId, text, from, size);
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                            @RequestBody ItemDto itemDto) {
        return itemService.create(ownerId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long id,
                                  @RequestBody CommentDto commentDto) {
        return itemService.postComment(userId, id, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                             @PathVariable Long id,
                             @RequestBody String json) {
        return itemService.patch(ownerId, id, json);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader(USER_ID_HEADER) Long ownerId,
                               @PathVariable Long id) {
        itemService.deleteById(ownerId, id);
    }
}