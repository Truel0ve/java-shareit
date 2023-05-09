package ru.practicum.shareit.features.request.model;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import ru.practicum.shareit.features.item.model.ItemForRequestDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ItemRequestDto {
    Long id;
    @NotBlank
    String description;
    LocalDateTime created = LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS);
    List<ItemForRequestDto> items;
}