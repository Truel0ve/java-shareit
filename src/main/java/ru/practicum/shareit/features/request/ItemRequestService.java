package ru.practicum.shareit.features.request;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.features.request.model.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createRequest(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestDto> getUserRequests(Long userId);

    List<ItemRequestDto> getAllRequests(Long userId, Pageable pageable);

    ItemRequestDto getRequestById(Long userId, Long requestId);
}