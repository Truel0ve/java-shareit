package ru.practicum.shareit.features.user;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.features.user.model.UserShortDto;
import ru.practicum.shareit.utility.UserValidator;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Getter
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUserDtoById(Long id) {
        return UserMapper.toUserDto(getUserById(id));
    }

    @Override
    public UserShortDto getUserShortById(Long id) {
        return new UserShortDto(userRepository.findUserById(id)
                .orElseThrow(() -> new ArgumentNotFoundException("The specified user id=" + id + " does not exist")));
    }

    @Override
    public User getUserById(Long id) {
        validateUserId(id);
        return userRepository.getReferenceById(id);
    }

    @Transactional
    @Override
    public UserDto create(UserDto userDto) {
        return UserMapper.toUserDto(userRepository.save(UserMapper.toUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto patch(Long id, String json) {
        validateUserId(id);
        User patchedUser = applyPatchUser(id, json);
        userRepository.patch(id, patchedUser.getName(), patchedUser.getEmail());
        return UserMapper.toUserDto(userRepository.getReferenceById(id));
    }

    @Transactional
    @Override
    public void deleteById(Long id) {
        validateUserId(id);
        userRepository.deleteById(id);
    }

    private User applyPatchUser(Long id, String json) {
        User user = userRepository.getReferenceById(id);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            if (jsonNode.has("name")) {
                patchUserName(jsonNode, user);
            }
            if (jsonNode.has("email")) {
                patchUserEmail(jsonNode, user);
            }
        } catch (JsonProcessingException e) {
            throw new ValidationException("User data processing error");
        }
        return user;
    }

    @Override
    public void validateUserId(Long id) {
        if (id == null || userRepository.findById(id).isEmpty()) {
            throw new ArgumentNotFoundException("The specified user id=" + id + " does not exist");
        }
    }

    private void patchUserName(JsonNode jsonNode, User user) {
        String newName = jsonNode.get("name").asText();
        UserValidator.validateName(newName);
        user.setName(newName);
    }

    private void patchUserEmail(JsonNode jsonNode, User user) {
        String newEmail = jsonNode.get("email").asText();
        UserValidator.validateEmail(newEmail);
        user.setEmail(newEmail);
    }
}