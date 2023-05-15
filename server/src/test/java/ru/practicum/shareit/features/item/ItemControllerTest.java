package ru.practicum.shareit.features.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.features.item.model.CommentDto;
import ru.practicum.shareit.features.item.model.ItemDto;

import java.nio.charset.StandardCharsets;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
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

    @Test
    void shouldPostItem() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class)))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(item))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .create(1L, item);
    }

    @Test
    void shouldGetItemById() throws Exception {
        when(itemService.getItemDtoById(anyLong(), anyLong()))
                .thenReturn(item);

        mvc.perform(get("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getItemDtoById(1L, 1L);
    }

    @Test
    void shouldGetAllOwnerItemsWithoutParams() throws Exception {
        when(itemService.getAllOwnerItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getAllOwnerItems(1L, 0, 10);
    }

    @Test
    void shouldGetAllOwnerItemsWithParams() throws Exception {
        when(itemService.getAllOwnerItems(anyLong(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getAllOwnerItems(1L, 1, 1);
    }

    @Test
    void shouldGetSearchWithoutParams() throws Exception {
        when(itemService.getSearch(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "Item"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getSearch(1L, "Item", 0, 10);
    }

    @Test
    void shouldGetSearchWithParams() throws Exception {
        when(itemService.getSearch(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(List.of(item));

        mvc.perform(get("/items/search", 1)
                        .header("X-Sharer-User-Id", 1)
                        .param("text", "Item")
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(item.getName())))
                .andExpect(jsonPath("$[0].description", is(item.getDescription())))
                .andExpect(jsonPath("$[0].available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .getSearch(1L, "Item", 1, 1);
    }

    @Test
    void shouldPatchItem() throws Exception {
        String json = "{ \"name\": \"New item\", \"description\": \"New description\", \"available\": \"false\" }";
        when(itemService.patch(anyLong(), anyLong(), anyString()))
                .thenReturn(item);

        mvc.perform(patch("/items/{id}", 1)
                        .content(json)
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.getAvailable())));

        Mockito.verify(itemService, Mockito.times(1))
                .patch(1L, 1L, json);
    }

    @Test
    void shouldDeleteItem() throws Exception {
        doNothing().when(itemService).deleteById(anyLong(), anyLong());

        mvc.perform(delete("/items/{id}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .deleteById(1L, 1L);
    }

    @Test
    void shouldPostComment() throws Exception {
        when(itemService.postComment(anyLong(), anyLong(), any(CommentDto.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/{id}/comment", 1)
                        .content(mapper.writeValueAsString(comment))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().toString())));

        Mockito.verify(itemService, Mockito.times(1))
                .postComment(1L, 1L, comment);
    }
}