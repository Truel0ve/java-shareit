package ru.practicum.shareit.features.user;

import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.features.user.model.UserShortDto;

import java.util.List;

public interface UserService {
    List<UserDto> getAll();

    UserDto getUserDtoById(Long id);

    User getUserById(Long id);

    UserShortDto getUserShortById(Long id);

    UserDto create(UserDto userDto);

    UserDto patch(Long id, String json);

    void deleteById(Long id);

    void validateUserId(Long id);
}