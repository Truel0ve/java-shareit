package ru.practicum.shareit.features.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.utility.RequestLogger;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        RequestLogger.logRequest(RequestMethod.GET, "/users");
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.GET, "/users/" + id);
        return userService.getUserDtoById(id);
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto userDto) {
        RequestLogger.logRequest(RequestMethod.POST, "/users");
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable Long id,
                             @RequestBody String json) {
        RequestLogger.logRequest(RequestMethod.PATCH, "/users/" + id);
        return userService.patch(id, json);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        RequestLogger.logRequest(RequestMethod.DELETE, "/users/" + id);
        userService.deleteById(id);
    }
}