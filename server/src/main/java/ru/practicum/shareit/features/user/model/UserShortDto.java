package ru.practicum.shareit.features.user.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserShortDto implements UserShort {
    private Long id;

    public UserShortDto(UserShort userShort) {
        this.id = userShort.getId();
    }

    public UserShortDto(Long id) {
        this.id = id;
    }
}