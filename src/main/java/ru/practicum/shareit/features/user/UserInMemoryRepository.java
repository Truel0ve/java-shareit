package ru.practicum.shareit.features.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.features.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
@Slf4j
public class UserInMemoryRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private Long newId = 0L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public User create(User user) {
        user.setId(++newId);
        users.put(user.getId(), user);
        log.info("A new user has been added: user id={}", user.getId());
        return user;
    }

    @Override
    public User patch(Long id, User user) {
        users.replace(id, user);
        log.info("User data has been patched: user id={}", id);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
        log.info("User has been deleted: user id={}", id);
    }
}