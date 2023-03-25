package ru.practicum.shareit.features.user;


import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ArgumentAlreadyExistsException;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.utility.UserValidator;

import java.util.List;

@Service
@RequiredArgsConstructor
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public User getById(Long id) {
        validateUserId(id);
        return userRepository.getById(id);
    }

    @Override
    public User create(User user) {
        validateSameEmail(user.getEmail());
        return userRepository.create(user);
    }

    @Override
    public User patch(Long id, String json) {
        validateUserId(id);
        return userRepository.patch(id, applyPatchUser(id, json));
    }

    @SneakyThrows
    private User applyPatchUser(Long id, String json) {
        User user = getById(id);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        JsonNode jsonNode = objectMapper.readTree(json);
        if (jsonNode.has("name")) {
            patchUserName(jsonNode, user);
        }
        if (jsonNode.has("email")) {
            patchUserEmail(jsonNode, user);
        }
        return user;
    }

    private void patchUserName(JsonNode jsonNode, User user) {
        String newName = jsonNode.get("name").asText();
        UserValidator.validateName(newName);
        user.setName(newName);
    }

    private void patchUserEmail(JsonNode jsonNode, User user) {
        String newEmail = jsonNode.get("email").asText();
        UserValidator.validateEmail(newEmail);
        if (!user.getEmail().equals(newEmail)) {
            validateSameEmail(newEmail);
        }
        user.setEmail(newEmail);
    }

    @Override
    public void deleteById(Long id) {
        validateUserId(id);
        userRepository.deleteById(id);
    }

    private void validateUserId(Long id) {
        if (id == null || userRepository.getById(id) == null) {
            throw new ArgumentNotFoundException("The specified user id=" + id + " does not exist");
        }
    }

    private void validateSameEmail(String email) {
        if (getAll()
                .stream()
                .anyMatch(someUser -> someUser.getEmail().equals(email))) {
            throw new ArgumentAlreadyExistsException("E-mail " + email + " is already taken by another user");
        }
    }
}