package ru.practicum.shareit.features.item;

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
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.ShareItGateway;
import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@MockBean(classes = ItemClient.class)
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    ItemDto item;
    CommentDto comment;

    @BeforeEach
    void setUp() {
        item = new ItemDto();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);

        comment = new CommentDto();
        comment.setAuthorName("John Doe");
        comment.setText("Comment");
        comment.setCreated(comment.getCreated().truncatedTo(ChronoUnit.SECONDS));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostItemThenNameIsNullOrBlank(String input) throws Exception {
        item.setName(input);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: name")));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostItemThenDescriptionIsNullOrBlank(String input) throws Exception {
        item.setDescription(input);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: description")));
    }

    @Test
    void shouldNotPostItemThenAvailableIsNull() throws Exception {
        item.setAvailable(null);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: available")));
    }

    @Test
    void shouldNotPostItemThenAvailableIsWrongValue() throws Exception {
        String json = "{ \"name\": \"Дрель\",\"description\": \"Простая дрель\", \"available\": wrong value\" }";

        mvc.perform(post("/items")
                        .content(json)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException));
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostCommentThenTextIsNullOrBlank(String input) throws Exception {
        comment.setText(input);

        mvc.perform(post("/items/{id}/comment", 1)
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: text")));
    }
}