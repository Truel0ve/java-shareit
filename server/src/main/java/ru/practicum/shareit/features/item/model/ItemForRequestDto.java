package ru.practicum.shareit.features.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.features.request.model.ItemRequest;

@Getter
@Setter
public class ItemForRequestDto implements ItemForRequest {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest itemRequest;
    private Long requestId;

    public ItemForRequestDto(ItemForRequest itemForRequest) {
        this.id = itemForRequest.getId();
        this.name = itemForRequest.getName();
        this.description = itemForRequest.getDescription();
        this.available = itemForRequest.getAvailable();
        this.requestId = itemForRequest.getItemRequest().getId();
    }
}