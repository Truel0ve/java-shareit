package ru.practicum.shareit.features.user;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.features.user.model.UserDto;
import ru.practicum.shareit.features.user.model.UserShortDto;

import javax.persistence.EntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserServiceIntegrationTest {
    final EntityManager entityManager;
    final UserService userService;
    final UserDto userDto = makeUserDto("John Doe", "john_doe@email.com");

    @Test
    void shouldCreateUser() {
        UserDto newUserDto = userService.create(userDto);
        User user = entityManager.find(User.class, newUserDto.getId());

        assertThat(user.getId(), notNullValue());
        assertThat(user.getName(), equalTo(newUserDto.getName()));
        assertThat(user.getEmail(), equalTo(newUserDto.getEmail()));
    }

    @Test
    void shouldNotCreateUserWithWrongCountOfNameChar() {
        assertThrows(DataIntegrityViolationException.class, () -> userService.create(
                makeUserDto("very_very_very_very_very_very_very_very_very_big_name", "john_doe@email.com")));
    }

    @Test
    void shouldGetUserDtoById() {
        User user = savedUser();
        UserDto expectedUserDto = userService.getUserDtoById(user.getId());

        assertThat(expectedUserDto.getId(), equalTo(user.getId()));
        assertThat(expectedUserDto.getName(), equalTo(user.getName()));
        assertThat(expectedUserDto.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldGetUserShortById() {
        User user = savedUser();
        Long userId = user.getId();
        UserShortDto userShort = userService.getUserShortById(userId);

        assertThat(userShort.getId(), equalTo(userId));

        entityManager.remove(user);

        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> userService.getUserShortById(userId));
        assertEquals("The specified user id=" + userId + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetAndDeleteUserById() {
        User user = savedUser();
        User expectedUser = userService.getUserById(user.getId());

        assertThat(expectedUser.getId(), equalTo(user.getId()));
        assertThat(expectedUser.getName(), equalTo(user.getName()));
        assertThat(expectedUser.getEmail(), equalTo(user.getEmail()));

        userService.deleteById(user.getId());
        User deletedUser = entityManager.find(User.class, user.getId());

        assertThat(deletedUser, equalTo(null));
    }

    @Test
    void shouldGetAllUsers() {
        List<UserDto> users = List.of(
                userDto,
                makeUserDto("Jane Doe", "jane_doe@email.com")
        );
        for (UserDto userDto : users) {
            entityManager.persist(UserMapper.toUser(userDto));
        }
        entityManager.flush();

        List<UserDto> expectedUsers = userService.getAll();

        assertThat(expectedUsers, hasSize(users.size()));
        for (UserDto userDto : expectedUsers) {
            assertThat(expectedUsers, hasItem(allOf(
                    hasProperty("id", equalTo(userDto.getId())),
                    hasProperty("name", equalTo(userDto.getName())),
                    hasProperty("email", equalTo(userDto.getEmail()))
            )));
        }
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(longs = 99)
    void shouldNotValidateUserId(Long input) {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> userService.validateUserId(input));
        assertEquals("The specified user id=" + input + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldPatchUser() {
        User user = savedUser();
        String json = "{ \"name\": \"Jane Doe\", \"email\": \"jane_doe@email.com\" }";
        UserDto patchedUser = userService.patch(user.getId(), json);

        assertThat(patchedUser.getId(), equalTo(user.getId()));
        assertThat(patchedUser.getName(), equalTo("Jane Doe"));
        assertThat(patchedUser.getEmail(), equalTo("jane_doe@email.com"));
    }

    @Test
    void shouldNotPatchUserWithWrongFields() {
        User user = savedUser();
        String json = "{ \"lastname\": \"Jane Doe\", \"mail\": \"jane_doe@email.com\" }";
        UserDto patchedUser = userService.patch(user.getId(), json);

        assertThat(patchedUser.getId(), equalTo(user.getId()));
        assertThat(patchedUser.getName(), equalTo(user.getName()));
        assertThat(patchedUser.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void shouldThrowValidationExceptionWithWrongJson() {
        User user = savedUser();
        String wrongJson = "name: Jane Doe, email: jane_doe@email.com";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.patch(user.getId(), wrongJson));
        assertEquals("User data processing error",
                exception.getMessage(), "Invalid message");
    }

    private UserDto makeUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setName(name);
        return userDto;
    }

    private User savedUser() {
        User user = UserMapper.toUser(userDto);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}