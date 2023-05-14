package ru.practicum.shareit.features.user;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.features.user.model.User;

@UtilityClass
public class UserMapper {

    public UserDto toUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
        return userDto;
    }

    public User toUser(UserDto userDto) {
        User user = new User();
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());
        return user;
    }
}