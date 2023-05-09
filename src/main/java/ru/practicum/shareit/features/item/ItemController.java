package ru.practicum.shareit.features.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.ItemDto;
import ru.practicum.shareit.utility.PageManager;
import ru.practicum.shareit.utility.RequestLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public List<ItemDto> getAllOwnerItems(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                          @RequestParam(required = false) Integer from,
                                          @RequestParam(required = false) Integer size) {
        if (from == null || size == null) {
            RequestLogger.logRequest(RequestMethod.GET, "/items");
            return itemService.getAllOwnerItems(ownerId, Pageable.unpaged());
        } else {
            RequestLogger.logRequest(RequestMethod.GET, "/items?from=" + from + "&size=" + size);
            return itemService.getAllOwnerItems(ownerId, PageManager.getPageable(from, size));
        }
    }

    @GetMapping("/{id}")
    public ItemDto getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.GET, "/items/" + id);
        return itemService.getItemDtoById(userId, id);
    }

    @GetMapping("/search")
    public List<ItemDto> getSearch(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @RequestParam("text") String text,
                                   @RequestParam(required = false) Integer from,
                                   @RequestParam(required = false) Integer size) {
        if (from == null || size == null) {
            RequestLogger.logRequest(RequestMethod.GET, "/items/search?text=" + text);
            return itemService.getSearch(userId, text, Pageable.unpaged());
        } else {
            RequestLogger.logRequest(RequestMethod.GET, "/items/search?text=" + text + "&from=" + from + "&size=" + size);
            return itemService.getSearch(userId, text, PageManager.getPageable(from, size));
        }
    }

    @PostMapping
    public ItemDto postItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                            @Valid @RequestBody ItemDto itemDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/items");
        return itemService.create(ownerId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long id,
                                  @Valid @RequestBody CommentDto commentDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/items/" + id + "/comment");
        return itemService.postComment(userId, id, commentDto);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                             @PathVariable Long id,
                             @RequestBody String json) {
        RequestLogger.logRequest(RequestMethod.PATCH, "/items/" + id);
        return itemService.patch(ownerId, id, json);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader(USER_ID_HEADER) Long ownerId,
                               @PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.DELETE, "/items/" + id);
        itemService.deleteById(ownerId, id);
    }
}