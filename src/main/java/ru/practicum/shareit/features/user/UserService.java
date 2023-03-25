package ru.practicum.shareit.features.user;

import ru.practicum.shareit.features.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getAll();

    User getById(Long id);

    User create(User user);

    User patch(Long id, String json);

    void deleteById(Long id);
}