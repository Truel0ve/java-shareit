package ru.practicum.shareit.features.item_request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.MethodArgumentNotValidException;
import ru.practicum.shareit.features.request.ItemRequestController;
import ru.practicum.shareit.features.request.ItemRequestService;
import ru.practicum.shareit.features.request.model.ItemRequestDto;
import ru.practicum.shareit.utility.PageManager;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mvc;
    ItemRequestDto request;

    @BeforeEach
    void setUp() {
        request = new ItemRequestDto();
        request.setDescription("Request");
        request.setItems(new ArrayList<>());
    }

    @Test
    void shouldPostRequest() throws Exception {
        when(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class)))
                .thenReturn(request);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(request.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .createRequest(1L, request);
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldNotPostRequestThenDescriptionIsNullOrBlank(String input) throws Exception {
        request.setDescription(input);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(request))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof MethodArgumentNotValidException))
                .andExpect(jsonPath("$.error", is("Invalid data format: description")));
    }

    @Test
    void shouldGetUserRequests() throws Exception {
        when(itemRequestService.getUserRequests(anyLong()))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(request.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getUserRequests(1L);
    }

    @ParameterizedTest
    @MethodSource("provideParamValues")
    void shouldGetAllRequestsWithoutParams(String param, String value) throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param(param, value))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(request.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllRequests(1L, Pageable.unpaged());
    }

    private static Stream<Arguments> provideParamValues() {
        return Stream.of(
                Arguments.of("from", "1"),
                Arguments.of("size", "1")
        );
    }

    @Test
    void shouldGetAllRequestsWithParams() throws Exception {
        when(itemRequestService.getAllRequests(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(request));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1)
                        .param("from", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(request.getDescription())))
                .andExpect(jsonPath("$[0].created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$[0].items", is(request.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getAllRequests(1L, PageManager.getPageable(1, 1));
    }

    @Test
    void shouldGetRequestById() throws Exception {
        when(itemRequestService.getRequestById(anyLong(), anyLong()))
                .thenReturn(request);

        mvc.perform(get("/requests/{requestId}", 1)
                        .header("X-Sharer-User-Id", 1))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(request.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(request.getDescription())))
                .andExpect(jsonPath("$.created", is(request.getCreated().toString())))
                .andExpect(jsonPath("$.items", is(request.getItems())));

        Mockito.verify(itemRequestService, Mockito.times(1))
                .getRequestById(1L, 1L);
    }
}