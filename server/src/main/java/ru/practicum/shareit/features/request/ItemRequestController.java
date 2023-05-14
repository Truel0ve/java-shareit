package ru.practicum.shareit.features.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.request.model.ItemRequestDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @RequestBody ItemRequestDto itemRequestDto) {
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(defaultValue = "0", required = false) Integer from,
                                               @RequestParam(defaultValue = "10", required = false) Integer size) {
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        return itemRequestService.getRequestById(userId, requestId);
    }
}