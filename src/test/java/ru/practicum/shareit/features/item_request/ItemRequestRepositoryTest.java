package ru.practicum.shareit.features.item_request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.features.request.ItemRequestRepository;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class ItemRequestRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    ItemRequestRepository requestRepository;
    ItemRequest userRequest;
    ItemRequest otherRequest;
    User user;

    @BeforeEach
    void setUp() {
        user = setUser("John Doe", "john_doe@email.com");
        User otherUser = setUser("Jane Doe", "jane_doe@email.com");
        userRequest = setRequest(user, "User request");
        otherRequest = setRequest(otherUser, "Other request");
    }

    @Test
    void shouldFindUserRequests() {
        List<ItemRequest> requests = requestRepository.findByUserIdOrderByCreatedDesc(user.getId());
        ItemRequest request = requests.get(0);

        assertThat(requests, hasSize(1));
        assertThat(request.getId(), equalTo(userRequest.getId()));
        assertThat(request.getUser(), equalTo(userRequest.getUser()));
        assertThat(request.getDescription(), equalTo(userRequest.getDescription()));
        assertThat(request.getCreated(), equalTo(userRequest.getCreated()));
    }

    @Test
    void shouldFindOtherRequests() {
        Page<ItemRequest> requests = requestRepository.findByUserIdNotOrderByCreatedDesc(user.getId(), Pageable.unpaged());
        ItemRequest request = requests.getContent().get(0);

        assertThat(requests.getTotalElements(), equalTo(1L));
        assertThat(request.getId(), equalTo(otherRequest.getId()));
        assertThat(request.getUser(), equalTo(otherRequest.getUser()));
        assertThat(request.getDescription(), equalTo(otherRequest.getDescription()));
        assertThat(request.getCreated(), equalTo(otherRequest.getCreated()));
    }

    private User setUser(String name, String email) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        entityManager.persist(user);
        entityManager.flush();
        return user;
    }

    private ItemRequest setRequest(User user, String description) {
        ItemRequest request = new ItemRequest();
        request.setUser(user);
        request.setDescription(description);
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entityManager.persist(request);
        entityManager.flush();
        return request;
    }
}