package ru.practicum.shareit.features.item;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.features.item.model.Item;
import ru.practicum.shareit.features.item.model.ItemForRequest;
import ru.practicum.shareit.features.item.model.ItemShort;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Find all items for request
    List<ItemForRequest> findByItemRequestId(Long requestId);

    // Find itemShort
    Optional<ItemShort> findItemById(Long id);

    // Find all owner items
    Page<Item> findByUserId(Long ownerId, Pageable pageable);

    // Search for text among items
    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND (UPPER(i.name) like UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) like UPPER(CONCAT('%', ?1, '%')))")
    Page<Item> getSearch(String text, Pageable pageable);

    // Patch item
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Item i " +
            "SET i.name = ?3, i.description = ?4, i.available = ?5 " +
            "WHERE i.user.id = ?1 AND i.id = ?2")
    void patch(Long ownerId,
               Long itemId,
               String name,
               String description,
               Boolean available);
}