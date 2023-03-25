package ru.practicum.shareit.features.item;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.ArgumentNotFoundException;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.user.UserRepository;
import ru.practicum.shareit.utility.ItemValidator;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Override
    public List<Item> getAllOwnerItems(Long ownerId) {
        validateUserId(ownerId);
        return itemRepository.getAllOwnerItems(ownerId);
    }

    @Override
    public Item getById(Long userId, Long itemId) {
        validateUserId(userId);
        validateItemId(itemId);
        return itemRepository.getById(itemId);
    }

    @Override
    public List<Item> getSearch(Long userId, String text) {
        validateUserId(userId);
        if (text.isBlank()) {
            return new ArrayList<>();
        } else {
            return itemRepository.getAll()
                    .stream()
                    .filter(Item::getAvailable)
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase()) ||
                            item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .collect(Collectors.toList());
        }
    }

    @Override
    public Item create(Long ownerId, Item item) {
        validateUserId(ownerId);
        return itemRepository.create(ownerId, item);
    }

    @Override
    public Item patch(Long ownerId, Long itemId, String json) {
        validateItemWithOwner(ownerId, itemId);
        return itemRepository.patch(ownerId, itemId, applyPatchItem(itemId, json));
    }

    @SneakyThrows
    private Item applyPatchItem(Long itemId, String json) {
        Item item = itemRepository.getById(itemId);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper
                .enable(SerializationFeature.INDENT_OUTPUT)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
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

    @Override
    public void deleteById(Long ownerId, Long itemId) {
        validateItemWithOwner(ownerId, itemId);
        itemRepository.deleteById(ownerId, itemId);
    }

    private void validateItemWithOwner(Long ownerId, Long itemId) {
        validateUserId(ownerId);
        validateItemId(itemId);
        validateOwnerId(ownerId, itemId);
    }

    private void validateUserId(Long ownerId) {
        if (ownerId == null || userRepository.getById(ownerId) == null) {
            throw new ArgumentNotFoundException("The specified user id=" + ownerId + " does not exist");
        }
    }

    private void validateItemId(Long itemId) {
        if (itemId == null || itemRepository.getById(itemId) == null) {
            throw new ArgumentNotFoundException("The specified item id=" + itemId + " does not exist");
        }
    }

    private void validateOwnerId(Long ownerId, Long itemId) {
        if (!itemRepository.getById(itemId).getOwnerId().equals(ownerId)) {
            throw new ArgumentNotFoundException("User id=" + ownerId + " is not the owner of item id=" + itemId);
        }
    }
}