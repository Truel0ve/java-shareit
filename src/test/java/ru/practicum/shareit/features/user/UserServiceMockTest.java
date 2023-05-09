package ru.practicum.shareit.features.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.features.user.model.UserDto;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceMockTest {
    @Spy
    @InjectMocks
    UserServiceImpl userService;
    @Mock
    UserRepository userRepository;
    UserDto userDto;
    User user;
    User patchedUser;

    @BeforeEach
    void setUser() {
        userDto = new UserDto();
        userDto.setName("John Doe");
        userDto.setEmail("john_doe@email.com");

        user = UserMapper.toUser(userDto);

        patchedUser = new User();
        patchedUser.setName("Jane Doe");
        patchedUser.setEmail("jane_doe@email.com");
    }

    @Test
    void shouldMappingUser() {
        User user = UserMapper.toUser(userDto);
        assertThat(user.getName(), equalTo(userDto.getName()));
        assertThat(user.getEmail(), equalTo(userDto.getEmail()));

        UserDto mappedUser = UserMapper.toUserDto(user);
        assertThat(mappedUser.getId(), equalTo(user.getId()));
        assertThat(mappedUser.getName(), equalTo(user.getName()));
        assertThat(mappedUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldCreateUser() {
        Mockito
                .when(userRepository.save(ArgumentMatchers.any(User.class)))
                .thenReturn(user);
        Assertions.assertEquals(userService.create(userDto), userDto);
    }

    @Test
    void shouldGetAllUsers() {
        Mockito
                .when(userRepository.findAll())
                .thenReturn(List.of(user));
        Assertions.assertEquals(userService.getAll(), List.of(userDto));
    }

    @Test
    void shouldNotValidateNullUserId() {
        Mockito
                .doThrow(new ArgumentNotFoundException("The specified user id=" + null + " does not exist"))
                .when(userService).validateUserId(null);
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> userService.validateUserId(null));
        assertEquals("The specified user id=" + null + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotValidateWrongUserId() {
        Mockito
                .doThrow(new ArgumentNotFoundException("The specified user id=" + 99 + " does not exist"))
                .when(userService).validateUserId(99L);
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> userService.validateUserId(99L));
        assertEquals("The specified user id=" + 99 + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetUserById() {
        setMockForUserValidation();
        Mockito
                .when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user);
        Assertions.assertEquals(userService.getUserById(anyLong()), user);
        Assertions.assertEquals(userService.getUserDtoById(anyLong()), userDto);
    }

    @Test
    void shouldDeleteUserById() {
        setMockForUserValidation();
        Mockito
                .doNothing()
                .when(userRepository).deleteById(anyLong());
        userService.deleteById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(anyLong());
    }

    @Test
    void shouldPatchUser() {
        setMockForUserValidation();
        Mockito
                .when(userRepository.getReferenceById(anyLong()))
                .thenReturn(user)
                .thenReturn(patchedUser);
        Mockito
                .doNothing()
                .when(userRepository).patch(anyLong(), anyString(), anyString());

        Assertions.assertEquals(userService.patch(anyLong(), anyString()), UserMapper.toUserDto(patchedUser));
        Mockito.verify(userRepository, Mockito.times(2))
                .getReferenceById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1))
                .patch(anyLong(), anyString(), anyString());
    }

    private void setMockForUserValidation() {
        Mockito
                .doNothing()
                .when(userService).validateUserId(anyLong());
    }
}