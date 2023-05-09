package ru.practicum.shareit.features.user;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.features.user.model.User;
import ru.practicum.shareit.features.user.model.UserShort;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class UserRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    UserRepository userRepository;
    User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setName("John Doe");
        user.setEmail("john_doe@email.com");
        entityManager.persist(user);
        entityManager.flush();
    }

    @Test
    void shouldFindUserShort() {
        Optional<UserShort> userShortOpt = userRepository.findUserById(user.getId());
        assertThat(userShortOpt.isPresent(), equalTo(true));

        UserShort userShort = userShortOpt.get();
        assertThat(userShort.getId(), equalTo(user.getId()));
    }

    @Test
    void shouldPatchUser() {
        userRepository.patch(
                user.getId(),
                "Jane Doe",
                "jane_doe@email.com");
        User patchedUser = entityManager.find(User.class, user.getId());

        assertThat(patchedUser.getId(), equalTo(user.getId()));
        assertThat(patchedUser.getName(), equalTo("Jane Doe"));
        assertThat(patchedUser.getEmail(), equalTo("jane_doe@email.com"));
    }
}