package ru.practicum.shareit.features.item_request;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.request.ItemRequestService;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.request.model.ItemRequestDto;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.EntityManager;

import java.util.List;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestServiceIntegrationTest {
    final EntityManager entityManager;
    final ItemRequestService itemRequestService;
    User owner;
    User requester;
    ItemRequestDto request;

    @BeforeEach
    void setUp() {
        owner = setUser("Owner", "owner@email.com");
        requester = setUser("Requester", "requester@email.com");
        request = itemRequestService.createRequest(requester.getId(), setRequest());
        Item item = setItem(owner, entityManager.find(ItemRequest.class, request.getId()));
        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void shouldCreateRequest() {
        assertThat(request.getId(), notNullValue());
        assertThat(request.getDescription(), equalTo("Request"));
        assertThat(request.getCreated(), notNullValue());
        assertThat(request.getItems(), equalTo(null));
    }

    @Test
    void shouldReturnUserRequests() {
        List<ItemRequestDto> requestDtoList = itemRequestService.getUserRequests(requester.getId());
        ItemRequestDto itemRequestDto = requestDtoList.get(0);

        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));
        assertThat(itemRequestDto.getItems().size(), equalTo(1));
    }

    @Test
    void shouldGetAllRequests() {
        List<ItemRequestDto> requestDtoList = itemRequestService.getAllRequests(owner.getId(), 0, 10);
        ItemRequestDto itemRequestDto = requestDtoList.get(0);

        assertThat(requestDtoList.size(), equalTo(1));
        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));
        assertThat(itemRequestDto.getItems().size(), equalTo(1));
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(requester.getId(), request.getId());

        assertThat(itemRequestDto.getId(), equalTo(request.getId()));
        assertThat(itemRequestDto.getDescription(), equalTo(request.getDescription()));
        assertThat(itemRequestDto.getCreated(), equalTo(request.getCreated()));
        assertThat(itemRequestDto.getItems().size(), equalTo(1));
    }

    @Test
    void shouldThrowExceptionAfterGettingRequestById() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemRequestService.getRequestById(requester.getId(), 99L));
        assertEquals("The specified item request id=" + 99 + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private ItemRequestDto setRequest() {
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Request");
        return itemRequestDto;
    }

    private Item setItem(User owner, ItemRequest itemRequest) {
        Item item = new Item();
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setUser(owner);
        item.setItemRequest(itemRequest);
        return item;
    }
}