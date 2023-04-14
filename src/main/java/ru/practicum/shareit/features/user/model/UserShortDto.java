package ru.practicum.shareit.features.user.model;

import lombok.Getter;
import lombok.Setter;
import ru.practicum.shareit.features.user.UserShort;

@Getter
@Setter
public class UserShortDto implements UserShort {
    private Long id;

    public UserShortDto(UserShort userShort) {
        this.id = userShort.getId();
    }
}