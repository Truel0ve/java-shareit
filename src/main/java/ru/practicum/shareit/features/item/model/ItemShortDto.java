package ru.practicum.shareit.features.item.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.features.item.ItemShort;

@Getter
@Setter
public class ItemShortDto implements ItemShort {
    private Long id;
    private String name;

    public ItemShortDto(ItemShort itemShort) {
        this.id = itemShort.getId();
        this.name = itemShort.getName();
    }
}