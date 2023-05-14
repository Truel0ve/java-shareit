package ru.practicum.shareit.features.request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.features.item.ItemService;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.request.model.ItemRequestDto;
import ru.practicum.shareit.features.user.UserService;
import ru.practicum.shareit.utility.PageManager;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestServiceImpl implements ItemRequestService {
    final ItemRequestRepository itemRequestRepository;
    final ItemService itemService;
    final UserService userService;

    @Transactional
    @Override
    public ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setUser(userService.getUserById(userId));
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    public List<ItemRequestDto> getUserRequests(Long userId) {
        userService.validateUserId(userId);
        return itemRequestRepository.findByUserIdOrderByCreatedDesc(userId).stream()
                .map(this::setItemsForRequest)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        userService.validateUserId(userId);
        return itemRequestRepository.findByUserIdNotOrderByCreatedDesc(userId, PageManager.getPageable(from, size))
                .map(this::setItemsForRequest)
                .getContent();
    }

    @Override
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userService.validateUserId(userId);
        return setItemsForRequest(itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ArgumentNotFoundException("The specified item request id=" + requestId + " does not exist")));
    }

    private ItemRequestDto setItemsForRequest(ItemRequest itemRequest) {
        ItemRequestDto itemRequestDto = ItemRequestMapper.toItemRequestDto(itemRequest);
        itemRequestDto.setItems(itemService.getItemsForRequest(itemRequest.getId()));
        return itemRequestDto;
    }
}