package ru.practicum.shareit.features.item.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.features.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class Item {
    private Long id;
    private Long ownerId;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    private Long requestId;
}