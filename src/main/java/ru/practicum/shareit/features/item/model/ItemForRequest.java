package ru.practicum.shareit.features.item.model;

import ru.practicum.shareit.features.request.model.ItemRequest;

public interface ItemForRequest {
    Long getId();

    String getName();

    String getDescription();

    Boolean getAvailable();

    ItemRequest getItemRequest();
}