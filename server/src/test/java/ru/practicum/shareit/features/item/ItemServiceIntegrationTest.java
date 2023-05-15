package ru.practicum.shareit.features.item;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.booking.BookingMapper;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingStatus;
import ru.practicum.shareit.features.item.model.*;
import ru.practicum.shareit.features.request.model.ItemRequest;
import ru.practicum.shareit.features.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.temporal.ChronoUnit;
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
class ItemServiceIntegrationTest {
    final EntityManager entityManager;
    final ItemService itemService;
    ItemDto itemDto;
    User owner;
    User booker;
    Booking lastBooking;
    Booking nextBooking;
    Comment comment;
    ItemRequest request;

    @BeforeEach
    void setUp() {
        owner = setUser("John Doe", "john_doe@email.com");
        booker = setUser("Jane Doe", "jane_doe@email.com");
        itemDto = itemService.create(owner.getId(), setItemDto(null));
        lastBooking = setBooking(
                booker,
                LocalDateTime.of(2023, Month.MAY, 1, 12, 0, 0),
                LocalDateTime.of(2023, Month.MAY, 2, 12, 0, 0),
                BookingStatus.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2023, Month.APRIL, 1, 12, 0, 0),
                LocalDateTime.of(2023, Month.APRIL, 2, 12, 0, 0),
                BookingStatus.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2023, Month.MAY, 3, 12, 0, 0),
                LocalDateTime.of(2023, Month.MAY, 4, 12, 0, 0),
                BookingStatus.REJECTED);
        nextBooking = setBooking(
                booker,
                LocalDateTime.of(2024, Month.MAY, 1, 12, 0, 0),
                LocalDateTime.of(2024, Month.MAY, 2, 12, 0, 0),
                BookingStatus.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2024, Month.MAY, 3, 12, 0, 0),
                LocalDateTime.of(2024, Month.MAY, 4, 12, 0, 0),
                BookingStatus.WAITING);
        setBooking(
                booker,
                LocalDateTime.of(2024, Month.APRIL, 3, 12, 0, 0),
                LocalDateTime.of(2024, Month.APRIL, 4, 12, 0, 0),
                BookingStatus.REJECTED);
        comment = setComment();
        request = setRequest();
    }

    @Test
    void shouldCreateItemWithoutRequestId() {
        assertThat(itemDto.getId(), notNullValue());
        assertThat(itemDto.getName(), equalTo("Item"));
        assertThat(itemDto.getDescription(), equalTo("Description"));
        assertThat(itemDto.getAvailable(), equalTo(true));
        assertThat(itemDto.getRequestId(), equalTo(null));
    }

    @Test
    void shouldCreateItemWithRequestId() {
        ItemDto itemWithRequest = itemService.create(owner.getId(), setItemDto(request.getId()));

        assertThat(itemWithRequest.getId(), notNullValue());
        assertThat(itemWithRequest.getName(), equalTo("Item"));
        assertThat(itemWithRequest.getDescription(), equalTo("Description"));
        assertThat(itemWithRequest.getAvailable(), equalTo(true));
        assertThat(itemWithRequest.getRequestId(), equalTo(request.getId()));
    }

    @Test
    void shouldNotCreateItemWithWrongRequestId() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.create(owner.getId(), setItemDto(99L)));
        assertEquals("The specified item request id=" + 99 + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetItemDtoByOwnerWithBookings() {
        ItemDto ownerItem = itemService.getItemDtoById(owner.getId(), itemDto.getId());

        assertThat(ownerItem.getId(), equalTo(itemDto.getId()));
        assertThat(ownerItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(ownerItem.getName(), equalTo(itemDto.getName()));
        assertThat(ownerItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(ownerItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(ownerItem.getLastBooking(), equalTo(BookingMapper.toBookingQueue(lastBooking)));
        assertThat(ownerItem.getNextBooking(), equalTo(BookingMapper.toBookingQueue(nextBooking)));
        assertThat(ownerItem.getComments(), hasSize(1));
    }

    @Test
    void shouldGetItemDtoByOwnerWithoutBookings() {
        entityManager.createQuery("DELETE from Booking where item = :item")
                .setParameter("item", entityManager.find(Item.class, itemDto.getId()))
                .executeUpdate();
        ItemDto ownerItem = itemService.getItemDtoById(owner.getId(), itemDto.getId());

        assertThat(ownerItem.getId(), equalTo(itemDto.getId()));
        assertThat(ownerItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(ownerItem.getName(), equalTo(itemDto.getName()));
        assertThat(ownerItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(ownerItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(ownerItem.getLastBooking(), equalTo(null));
        assertThat(ownerItem.getNextBooking(), equalTo(null));
        assertThat(ownerItem.getComments(), hasSize(1));
    }

    @Test
    void shouldGetItemDtoByBookerWithoutBookings() {
        ItemDto ownerItem = itemService.getItemDtoById(booker.getId(), itemDto.getId());

        assertThat(ownerItem.getId(), equalTo(itemDto.getId()));
        assertThat(ownerItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(ownerItem.getName(), equalTo(itemDto.getName()));
        assertThat(ownerItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(ownerItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(ownerItem.getLastBooking(), equalTo(null));
        assertThat(ownerItem.getNextBooking(), equalTo(null));
        assertThat(ownerItem.getComments(), hasSize(1));
    }

    @Test
    void shouldNotGetItemDtoByWrongId() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.getItemDtoById(booker.getId(), 99L));
        assertEquals("The specified item id=" + 99 + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetAllOwnerItems() {
        List<ItemDto> items = itemService.getAllOwnerItems(owner.getId(), 0, 10);
        ItemDto item = items.get(0);

        assertThat(items, hasSize(1));
        assertThat(item.getId(), equalTo(itemDto.getId()));
        assertThat(item.getOwnerId(), equalTo(owner.getId()));
        assertThat(item.getName(), equalTo(itemDto.getName()));
        assertThat(item.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(item.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(item.getLastBooking(), equalTo(BookingMapper.toBookingQueue(lastBooking)));
        assertThat(item.getNextBooking(), equalTo(BookingMapper.toBookingQueue(nextBooking)));
        assertThat(item.getComments(), hasSize(1));
    }

    @Test
    void shouldGetItemShortById() {
        Long itemId = itemDto.getId();
        ItemShortDto itemShortDto = itemService.getItemShortById(itemId);

        assertThat(itemShortDto.getId(), equalTo(itemId));
        assertThat(itemShortDto.getName(), equalTo(itemDto.getName()));

        entityManager.createQuery("DELETE from Item where id = :id")
                .setParameter("id", itemId)
                .executeUpdate();

        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.getItemShortById(itemId));
        assertEquals("The specified item id=" + itemId + " does not exist",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldPatchItemByOwner() {
        String json = "{ \"name\": \"New item\", \"description\": \"New description\", \"available\": \"false\" }";
        ItemDto patchedItem = itemService.patch(owner.getId(), itemDto.getId(), json);

        assertThat(patchedItem.getId(), equalTo(itemDto.getId()));
        assertThat(patchedItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(patchedItem.getName(), equalTo("New item"));
        assertThat(patchedItem.getDescription(), equalTo("New description"));
        assertThat(patchedItem.getAvailable(), equalTo(false));
    }

    @Test
    void shouldNotPatchItemByOwnerWithWrongFields() {
        String json = "{ \"item name\": \"New item\", \"item description\": \"New description\", \"item available\": \"false\" }";
        ItemDto patchedItem = itemService.patch(owner.getId(), itemDto.getId(), json);

        assertThat(patchedItem.getId(), equalTo(itemDto.getId()));
        assertThat(patchedItem.getOwnerId(), equalTo(owner.getId()));
        assertThat(patchedItem.getName(), equalTo(itemDto.getName()));
        assertThat(patchedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(patchedItem.getAvailable(), equalTo(itemDto.getAvailable()));
    }

    @Test
    void shouldNotPatchItemByOwnerWithWrongJson() {
        String wrongJson = "name: New item, description: New description, available: false";

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.patch(owner.getId(), itemDto.getId(), wrongJson));
        assertEquals("Item data processing error",
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotPatchItemByOtherUser() {
        String json = "{ \"name\": \"New item\", \"description\": \"New description\", \"available\": \"false\" }";

        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.patch(booker.getId(), itemDto.getId(), json));
        assertEquals("User id=" + booker.getId() + " is not the owner of item id=" + itemDto.getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldDeleteItemByOwner() {
        itemService.deleteById(owner.getId(), itemDto.getId());
        Item deletedItem = entityManager.find(Item.class, itemDto.getId());

        assertThat(deletedItem, equalTo(null));
    }

    @Test
    void shouldNotDeleteItemByBooker() {
        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.deleteById(booker.getId(), itemDto.getId()));
        assertEquals("User id=" + booker.getId() + " is not the owner of item id=" + itemDto.getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldGetSearch() {
        List<ItemDto> presentList = itemService.getSearch(booker.getId(),"Item", 0, 10);
        assertThat(presentList, hasSize(1));
        assertThat(presentList, hasItem(itemDto));

        List<ItemDto> emptyList = itemService.getSearch(booker.getId(),"Empty", 0, 10);
        assertThat(emptyList, hasSize(0));
    }

    @Test
    void shouldNotGetSearch() {
        List<ItemDto> emptyList = itemService.getSearch(booker.getId(), " ", 0, 10);
        assertThat(emptyList, hasSize(0));
    }

    @Test
    void shouldGetItemsForRequest() {
        ItemDto itemDto = itemService.create(owner.getId(), setItemDto(request.getId()));
        List<ItemForRequestDto> items = itemService.getItemsForRequest(request.getId());
        ItemForRequestDto searchedItem = items.get(0);

        assertThat(items, hasSize(1));
        assertThat(searchedItem.getId(), equalTo(itemDto.getId()));
        assertThat(searchedItem.getName(), equalTo(itemDto.getName()));
        assertThat(searchedItem.getDescription(), equalTo(itemDto.getDescription()));
        assertThat(searchedItem.getAvailable(), equalTo(itemDto.getAvailable()));
        assertThat(searchedItem.getRequestId(), equalTo(itemDto.getRequestId()));
    }

    @Test
    void shouldPostCommentByBooker() {
        CommentDto newComment = setNewComment();
        CommentDto postedComment = itemService.postComment(booker.getId(), itemDto.getId(), newComment);

        assertThat(postedComment.getId(), notNullValue());
        assertThat(postedComment.getAuthorName(), equalTo(comment.getUser().getName()));
        assertThat(postedComment.getText(), equalTo(comment.getText()));
        assertThat(postedComment.getCreated(), equalTo(comment.getCreated()));
    }

    @Test
    void shouldNotPostCommentByOwner() {
        CommentDto newComment = setNewComment();

        ArgumentNotFoundException exception = assertThrows(ArgumentNotFoundException.class,
                () -> itemService.postComment(owner.getId(), itemDto.getId(), newComment));
        assertEquals("Unable to add a comment. User id=" + owner.getId() + " is the owner of item id=" + itemDto.getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotPostCommentByNotBooker() {
        User notBooker = setUser("New user", "new_user@email.com");
        CommentDto newComment = setNewComment();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.postComment(notBooker.getId(), itemDto.getId(), newComment));
        assertEquals("User id=" + notBooker.getId() + " has not booked item id=" + itemDto.getId(),
                exception.getMessage(), "Invalid message");
    }

    @Test
    void shouldNotPostCommentByStealBooker() {
        User stealBooker = setUser("New user", "new_user@email.com");
        setBooking(
                stealBooker,
                LocalDateTime.of(2023, Month.MAY, 6, 12, 0, 0),
                LocalDateTime.of(2024, Month.FEBRUARY, 1, 12, 0, 0),
                BookingStatus.APPROVED);
        CommentDto newComment = setNewComment();

        ValidationException exception = assertThrows(ValidationException.class,
                () -> itemService.postComment(stealBooker.getId(), itemDto.getId(), newComment));
        assertEquals("User id=" + stealBooker.getId() + " is still the holder of item id=" + itemDto.getId(),
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

    private ItemDto setItemDto(Long requestId) {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Item");
        itemDto.setDescription("Description");
        itemDto.setAvailable(true);
        if (requestId != null) {
            itemDto.setRequestId(requestId);
        }
        return itemDto;
    }

    private Booking setBooking(User booker, LocalDateTime start, LocalDateTime end, BookingStatus status) {
        Booking booking = new Booking();
        booking.setItem(entityManager.find(Item.class, itemDto.getId()));
        booking.setUser(booker);
        booking.setStatus(status);
        booking.setStart(start);
        booking.setEnd(end);
        entityManager.persist(booking);
        entityManager.flush();
        return booking;
    }

    private Comment setComment() {
        Comment comment = new Comment();
        comment.setText("Comment");
        comment.setUser(booker);
        comment.setItem(entityManager.find(Item.class, itemDto.getId()));
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entityManager.persist(comment);
        entityManager.flush();
        return comment;
    }

    private CommentDto setNewComment() {
        CommentDto comment = new CommentDto();
        comment.setText("Comment");
        comment.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        return comment;
    }

    private ItemRequest setRequest() {
        ItemRequest request = new ItemRequest();
        request.setUser(booker);
        request.setDescription("Request");
        request.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));
        entityManager.persist(request);
        entityManager.flush();
        return request;
    }
}