package ru.practicum.shareit.features.item;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.features.booking.BookingMapper;
import ru.practicum.shareit.features.booking.BookingRepository;
import ru.practicum.shareit.features.booking.model.Booking;
import ru.practicum.shareit.features.booking.model.BookingStatus;
import ru.practicum.shareit.features.item.model.*;
import ru.practicum.shareit.features.request.ItemRequestRepository;
import ru.practicum.shareit.features.user.UserService;
import ru.practicum.shareit.utility.ItemValidator;
import ru.practicum.shareit.utility.PageManager;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;
    private final UserService userService;

    @Override
    public List<ItemDto> getAllOwnerItems(Long ownerId, Integer from, Integer size) {
        return itemRepository.findByUserIdOrderById(ownerId, PageManager.getPageable(from, size)).stream()
                .map(ItemMapper::toItemDto)
                .map(this::setCommentsForItemDto)
                .peek(this::setBookingsForItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemDtoById(Long userId, Long itemId) {
        Item item = getItemById(itemId);
        ItemDto itemDto = ItemMapper.toItemDto(item);
        if (isOwnerOfItem(userId, item)) {
            setBookingsForItemDto(itemDto);
        }
        setCommentsForItemDto(itemDto);
        return itemDto;
    }

    @Override
    public List<ItemDto> getSearch(Long userId, String text, Integer from, Integer size) {
        userService.validateUserId(userId);
        if (!text.isBlank()) {
            return itemRepository.getSearch(text, PageManager.getPageable(from, size)).stream()
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    @Transactional
    @Override
    public ItemDto create(Long ownerId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto);
        item.setUser(userService.getUserById(ownerId));
        if (itemDto.getRequestId() != null) {
            item.setItemRequest(itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new ArgumentNotFoundException("The specified item request id=" + itemDto.getRequestId() + " does not exist")));
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto patch(Long ownerId, Long itemId, String json) {
        if (!isOwnerOfItem(ownerId, getItemById(itemId))) {
            throw new ArgumentNotFoundException("User id=" + ownerId + " is not the owner of item id=" + itemId);
        }
        Item patchedItem = applyPatchItem(itemId, json);
        itemRepository.patch(
                ownerId, itemId, patchedItem.getName(), patchedItem.getDescription(), patchedItem.getAvailable());
        return ItemMapper.toItemDto(getItemById(itemId));
    }

    @Transactional
    @Override
    public void deleteById(Long ownerId, Long itemId) {
        if (!isOwnerOfItem(ownerId, getItemById(itemId))) {
            throw new ArgumentNotFoundException("User id=" + ownerId + " is not the owner of item id=" + itemId);
        }
        itemRepository.deleteById(itemId);
    }

    @Transactional
    @Override
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentDto) {
        validateUserForComment(userId, itemId);
        Comment newComment = CommentMapper.toComment(commentDto);
        newComment.setUser(userService.getUserById(userId));
        newComment.setItem(itemRepository.getReferenceById(itemId));
        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    public ItemShortDto getItemShortById(Long itemId) {
        return new ItemShortDto(itemRepository.findItemById(itemId)
                .orElseThrow(() -> new ArgumentNotFoundException("The specified item id=" + itemId + " does not exist")));
    }

    @Override
    public List<ItemForRequestDto> getItemsForRequest(Long requestId) {
        return itemRepository.findByItemRequestId(requestId).stream()
                .map(ItemForRequestDto::new)
                .collect(Collectors.toList());
    }

    @Override
    public Item getItemById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new ArgumentNotFoundException("The specified item id=" + id + " does not exist"));
    }

    private Item applyPatchItem(Long itemId, String json) {
        Item item = getItemById(itemId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            JsonNode jsonNode = objectMapper.readTree(json);
            if (jsonNode.has("name")) {
                patchItemName(jsonNode, item);
            }
            if (jsonNode.has("description")) {
                patchItemDescription(jsonNode, item);
            }
            if (jsonNode.has("available")) {
                patchItemAvailable(jsonNode, item);
            }
        } catch (JsonProcessingException e) {
            throw new ValidationException("Item data processing error");
        }
        return item;
    }

    private void patchItemName(JsonNode jsonNode, Item item) {
        String newName = jsonNode.get("name").asText();
        ItemValidator.validateName(newName);
        item.setName(newName);
    }

    private void patchItemDescription(JsonNode jsonNode, Item item) {
        String newDescription = jsonNode.get("description").asText();
        ItemValidator.validateDescription(newDescription);
        item.setDescription(newDescription);
    }

    private void patchItemAvailable(JsonNode jsonNode, Item item) {
        Boolean newAvailable = jsonNode.get("available").asBoolean();
        ItemValidator.validateAvailable(newAvailable);
        item.setAvailable(newAvailable);
    }

    private void setBookingsForItemDto(ItemDto itemDto) {
        List<Booking> itemBookings = bookingRepository.findAllByItemIdOrderByStartAsc(itemDto.getId());
        if (!itemBookings.isEmpty()) {
            Optional<Booking> lastBookingOpt = itemBookings.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED)
                            && booking.getStart().isBefore(LocalDateTime.now()))
                    .reduce((first, second) -> second);
            lastBookingOpt.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.toBookingQueue(booking)));
            Optional<Booking> nextBookingOpt = itemBookings.stream()
                    .filter(booking -> !booking.getStatus().equals(BookingStatus.REJECTED)
                            && booking.getStart().isAfter(LocalDateTime.now()))
                    .findFirst();
            nextBookingOpt.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.toBookingQueue(booking)));
        }
    }

    private ItemDto setCommentsForItemDto(ItemDto itemDto) {
        itemDto.setComments(commentRepository.findByItemId(itemDto.getId())
                .stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList()));
        return itemDto;
    }

    private void validateUserForComment(Long userId, Long itemId) {
        if (isOwnerOfItem(userId, getItemById(itemId))) {
            throw new ArgumentNotFoundException(
                    "Unable to add a comment. User id=" + userId + " is the owner of item id=" + itemId);
        }
        if (!isUserWasBooker(userId, itemId)) {
            throw new ValidationException("User id=" + userId + " is still the holder of item id=" + itemId);
        }
    }

    private boolean isOwnerOfItem(Long ownerId, Item item) {
        return item.getUser().getId().equals(ownerId);
    }

    private boolean isUserWasBooker(Long userId, Long itemId) {
        List<Booking> bookings = bookingRepository.findByUserIdAndItemIdOrderByStartAsc(userId, itemId);
        if (!bookings.isEmpty()) {
            return bookings.stream()
                    .anyMatch(booking -> booking.getEnd().isBefore(LocalDateTime.now()));
        } else {
            throw new ValidationException("User id=" + userId + " has not booked item id=" + itemId);
        }
    }
}