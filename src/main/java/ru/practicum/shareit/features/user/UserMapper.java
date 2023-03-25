package ru.practicum.shareit.features.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.features.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
    }

    public User toUser(UserDto userDto) {
        return User.builder()
                .name(userDto.getName())
                .email(userDto.getEmail())
                .build();
    }
}