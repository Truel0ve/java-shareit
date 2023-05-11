package ru.practicum.shareit.features.request;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.features.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    // Find all user requests
    List<ItemRequest> findByUserIdOrderByCreatedDesc(Long userId);

    // Find all other requests with pages
    Page<ItemRequest> findByUserIdNotOrderByCreatedDesc(Long userId, Pageable pageable);
}