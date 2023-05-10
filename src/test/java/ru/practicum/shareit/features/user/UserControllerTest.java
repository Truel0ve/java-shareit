package ru.practicum.shareit.features.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.features.user.model.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mvc;
    UserDto user;

    @BeforeEach
    void setUp() {
        user = new UserDto();
        user.setName("John Doe");
        user.setEmail("john_doe@email.com");
    }

    @Test
    void shouldPostUser() throws Exception {
        when(userService.create(any(UserDto.class)))
                .thenReturn(user);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .create(user);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostUserThenNameIsNullOrBlank(String input) throws Exception {
        user.setName(input);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: name")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostUserThenEmailIsNullOrBlank(String input) throws Exception {
        user.setEmail(input);

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: email")));
    }

    @Test
    void shouldNotPostUserThenEmailHasWrongPattern() throws Exception {
        user.setEmail("john_doeывадлшр@email.com");

        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(user))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: email")));
    }

    @Test
    void shouldGetUserById() throws Exception {
        when(userService.getUserDtoById(anyLong()))
                .thenReturn(user);

        mvc.perform(get("/users/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(user.getName())))
                .andExpect(jsonPath("$.email", is(user.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .getUserDtoById(1L);
    }

    @Test
    void shouldGetAllUsers() throws Exception {
        when(userService.getAll())
                .thenReturn(List.of(user));

        mvc.perform(get("/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(user.getName())))
                .andExpect(jsonPath("$[0].email", is(user.getEmail())));

        Mockito.verify(userService, Mockito.times(1))
                .getAll();
    }

    @Test
    void shouldPatchUser() throws Exception {
        String json = "{ \"name\": \"Jane Doe\", \"email\": \"jane_doe@email.com\" }";
        when(userService.patch(anyLong(), anyString()))
                .thenReturn(user);

        mvc.perform(patch("/users/{id}", 1)
                        .content(json)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(user.getId()), Long.class))
                .andExpect(jsonPath("$.name", is("John Doe")))
                .andExpect(jsonPath("$.email", is("john_doe@email.com")));

        Mockito.verify(userService, Mockito.times(1))
                .patch(1L, json);
    }

    @Test
    void shouldDeleteUser() throws Exception {
        doNothing().when(userService).deleteById(anyLong());

        mvc.perform(delete("/users/{id}", 1))
                .andExpect(status().isOk());

        Mockito.verify(userService, Mockito.times(1))
                .deleteById(1L);
    }
}
