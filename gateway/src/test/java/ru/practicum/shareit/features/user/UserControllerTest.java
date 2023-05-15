package ru.practicum.shareit.features.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.features.user.model.UserDto;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@MockBean(classes = UserClient.class)
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    UserDto user;

    @BeforeEach
    void setUp() {
        user = new UserDto();
        user.setName("John Doe");
        user.setEmail("john_doe@email.com");
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
}
