package ru.practicum.shareit.features.user;

import ru.practicum.shareit.features.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getAll();

    User getById(Long id);

    User create(User user);

    User patch(Long id, User user);

    void deleteById(Long id);
}