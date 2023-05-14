package ru.practicum.shareit.features.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.request.model.ItemRequestDto;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> postRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                              @Valid @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestClient.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestClient.getUserRequests(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemRequestClient.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestClient.getRequestById(userId, requestId);
    }
}