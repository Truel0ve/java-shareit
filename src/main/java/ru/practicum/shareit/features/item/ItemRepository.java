package ru.practicum.shareit.features.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.features.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {

    // Find itemShort
    Optional<ItemShort> findItemById(Long id);

    // Find all owner items
    List<Item> findByUserId(Long ownerId);

    // Search for text among items
    @Query("SELECT i " +
            "FROM Item i " +
            "WHERE i.available = true " +
            "AND (UPPER(i.name) like UPPER(CONCAT('%', ?1, '%')) " +
            "OR UPPER(i.description) like UPPER(CONCAT('%', ?1, '%')))")
    List<Item> getSearch(String text);

    // Patch item
    @Modifying
    @Query("UPDATE Item i " +
            "SET i.name = ?3, i.description = ?4, i.available = ?5 " +
            "WHERE i.user.id = ?1 AND i.id = ?2")
    void patch(Long ownerId,
               Long itemId,
               String name,
               String description,
               Boolean available);
}