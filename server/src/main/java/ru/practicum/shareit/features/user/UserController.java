package ru.practicum.shareit.features.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.features.user.model.UserDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAllUsers() {
        return userService.getAll();
    }

    @GetMapping("/{id}")
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserDtoById(id);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto userDto) {
        return userService.create(userDto);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable Long id,
                             @RequestBody String json) {
        return userService.patch(id, json);
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        userService.deleteById(id);
    }
}