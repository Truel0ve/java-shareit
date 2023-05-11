package ru.practicum.shareit.features.item;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.features.item.model.Comment;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.model.User;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@DataJpaTest
@FieldDefaults(level = AccessLevel.PRIVATE)
class CommentRepositoryTest {
    @Autowired
    TestEntityManager entityManager;
    @Autowired
    CommentRepository commentRepository;
    Item item;
    Comment comment;

    @BeforeEach
    void setUp() {
        User owner = setUser("John Doe", "john_doe@email.com");
        User commentator = setUser("Jane Doe", "jane_doe@email.com");

        item = new Item();
        item.setUser(owner);
        item.setName("Item");
        item.setDescription("Description");
        item.setAvailable(true);
        entityManager.persist(item);
        entityManager.flush();

        comment = new Comment();
        comment.setItem(item);
        comment.setUser(commentator);
        comment.setText("Comment");
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entityManager.persist(comment);
        entityManager.flush();
    }

    @Test
    void shouldFindCommentsByItemId() {
        List<Comment> comments = commentRepository.findByItemId(item.getId());
        Comment searchedComment = comments.get(0);

        assertThat(comments, hasSize(1));
        assertThat(searchedComment.getId(), equalTo(comment.getId()));
        assertThat(searchedComment.getItem().getId(), equalTo(comment.getItem().getId()));
        assertThat(searchedComment.getUser().getId(), equalTo(comment.getUser().getId()));
        assertThat(searchedComment.getText(), equalTo(comment.getText()));
        assertThat(searchedComment.getCreated(), equalTo(comment.getCreated()));
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