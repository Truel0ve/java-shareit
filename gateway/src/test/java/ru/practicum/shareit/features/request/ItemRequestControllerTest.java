package ru.practicum.shareit.features.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
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
import ru.practicum.shareit.features.request.model.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@MockBean(classes = ItemRequestClient.class)
@ContextConfiguration(classes = ShareItGateway.class)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestControllerTest {
    @Autowired
    ObjectMapper mapper;
    @Autowired
    MockMvc mvc;
    ItemRequestDto request;

    @BeforeEach
    void setUp() {
        request = new ItemRequestDto();
        request.setDescription("Request");
        request.setItems(new ArrayList<>());
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
}