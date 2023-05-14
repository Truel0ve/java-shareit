package ru.practicum.shareit.features.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.ItemDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getAllOwnerItems(@RequestHeader(USER_ID_HEADER) Long ownerId,
                                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                                   @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemClient.getAllOwnerItems(ownerId, from, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader(USER_ID_HEADER) Long userId,
                               @PathVariable Long id) {
        return itemClient.getItemDtoById(userId, id);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getSearch(@RequestHeader(USER_ID_HEADER) Long userId,
                                   @RequestParam("text") String text,
                                   @RequestParam(defaultValue = "0", required = false) Integer from,
                                   @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemClient.getSearch(userId, text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                            @Valid @RequestBody ItemDto itemDto) {
        return itemClient.create(ownerId, itemDto);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(USER_ID_HEADER) Long userId,
                                  @PathVariable Long id,
                                  @Valid @RequestBody CommentDto commentDto) {
        return itemClient.postComment(userId, id, commentDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(@RequestHeader(USER_ID_HEADER) Long ownerId,
                             @PathVariable Long id,
                             @RequestBody String json) {
        return itemClient.patch(ownerId, id, json);
    }

    @DeleteMapping("/{id}")
    public void deleteItemById(@RequestHeader(USER_ID_HEADER) Long ownerId,
                               @PathVariable Long id) {
        itemClient.deleteById(ownerId, id);
    }
}