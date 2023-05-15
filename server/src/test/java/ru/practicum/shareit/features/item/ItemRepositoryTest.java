package ru.practicum.shareit.features.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.item.model.ItemForRequest;
import ru.practicum.shareit.features.item.model.ItemShort;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ItemRepository itemRepository;
    Item item;
    ItemRequest request;
    User owner;

    @BeforeEach
    void setUp() {
        owner = setUser("John Doe", "john_doe@email.com");
        User requester = setUser("Jane Doe", "jane_doe@email.com");

        request = new ItemRequest();
        request.setUser(requester);
        request.setDescription("Description");
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entityManager.persist(request);
        entityManager.flush();

        item = new Item();
        item.setUser(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setItemRequest(request);
        entityManager.persist(item);
        entityManager.flush();
    }

    @Test
    void shouldFindByItemRequestId() {
        List<ItemForRequest> items = itemRepository.findByItemRequestId(request.getId());
        ItemForRequest itemForRequest = items.get(0);

        assertThat(items, hasSize(1));
        assertThat(itemForRequest.getId(), equalTo(item.getId()));
        assertThat(itemForRequest.getName(), equalTo(item.getName()));
        assertThat(itemForRequest.getDescription(), equalTo(item.getDescription()));
        assertThat(itemForRequest.getAvailable(), equalTo(item.getAvailable()));
        assertThat(itemForRequest.getItemRequest(), equalTo(item.getItemRequest()));
    }

    @Test
    void shouldFindItemById() {
        Optional<ItemShort> itemShortOpt = itemRepository.findItemById(item.getId());
        assertThat(itemShortOpt.isPresent(), equalTo(true));

        ItemShort itemShort = itemShortOpt.get();
        assertThat(itemShort.getId(), equalTo(item.getId()));
        assertThat(itemShort.getName(), equalTo(item.getName()));
    }

    @Test
    void shouldFindByUserId() {
        Page<Item> items = itemRepository.findByUserIdOrderById(owner.getId(), Pageable.unpaged());
        Item searchedItem = items.getContent().get(0);

        assertThat(items.getTotalElements(), equalTo(1L));
        assertThat(searchedItem.getId(), equalTo(item.getId()));
        assertThat(searchedItem.getUser(), equalTo(item.getUser()));
        assertThat(searchedItem.getName(), equalTo(item.getName()));
        assertThat(searchedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(searchedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(searchedItem.getItemRequest(), equalTo(item.getItemRequest()));
    }

    @Test
    void shouldGetSearchItemByName() {
        Page<Item> items = itemRepository.getSearch("Item", Pageable.unpaged());
        Item searchedItem = items.getContent().get(0);

        assertThat(items.getTotalElements(), equalTo(1L));
        assertThat(searchedItem.getId(), equalTo(item.getId()));
        assertThat(searchedItem.getUser(), equalTo(item.getUser()));
        assertThat(searchedItem.getName(), equalTo(item.getName()));
        assertThat(searchedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(searchedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(searchedItem.getItemRequest(), equalTo(item.getItemRequest()));
    }

    @Test
    void shouldGetSearchItemByDescription() {
        Page<Item> items = itemRepository.getSearch("Description", Pageable.unpaged());
        Item searchedItem = items.getContent().get(0);

        assertThat(items.getTotalElements(), equalTo(1L));
        assertThat(searchedItem.getId(), equalTo(item.getId()));
        assertThat(searchedItem.getUser(), equalTo(item.getUser()));
        assertThat(searchedItem.getName(), equalTo(item.getName()));
        assertThat(searchedItem.getDescription(), equalTo(item.getDescription()));
        assertThat(searchedItem.getAvailable(), equalTo(item.getAvailable()));
        assertThat(searchedItem.getItemRequest(), equalTo(item.getItemRequest()));
    }

    @Test
    void shouldPatchItem() {
        itemRepository.patch(
                owner.getId(),
                item.getId(),
                "New item",
                "New description",
                false);
        Item patchedItem = entityManager.find(Item.class, item.getId());

        assertThat(patchedItem.getId(), equalTo(item.getId()));
        assertThat(patchedItem.getUser().getId(), equalTo(item.getUser().getId()));
        assertThat(patchedItem.getName(), equalTo("New item"));
        assertThat(patchedItem.getDescription(), equalTo("New description"));
        assertThat(patchedItem.getAvailable(), equalTo(false));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }
}