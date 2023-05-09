package ru.practicum.shareit.features.request;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.request.model.ItemRequestDto;
import ru.practicum.shareit.utility.PageManager;
import ru.practicum.shareit.utility.RequestLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final ItemRequestService itemRequestService;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto postRequest(@RequestHeader(USER_ID_HEADER) Long userId,
                                      @Valid @RequestBody ItemRequestDto itemRequestDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/requests");
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID_HEADER) Long userId) {
        RequestLogger.logRequest(RequestMethod.GET, "/requests");
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID_HEADER) Long userId,
                                               @RequestParam(required = false) Integer from,
                                               @RequestParam(required = false) Integer size) {
        if (from == null || size == null) {
            RequestLogger.logRequest(RequestMethod.GET, "/requests/all");
            return itemRequestService.getAllRequests(userId, Pageable.unpaged());
        } else  {
            RequestLogger.logRequest(RequestMethod.GET, "/requests/all?from=" + from + "&size=" + size);
            return itemRequestService.getAllRequests(userId, PageManager.getPageable(from, size));
        }
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(USER_ID_HEADER) Long userId,
                                         @PathVariable Long requestId) {
        RequestLogger.logRequest(RequestMethod.GET, "/requests/" + requestId);
        return itemRequestService.getRequestById(userId, requestId);
    }
}