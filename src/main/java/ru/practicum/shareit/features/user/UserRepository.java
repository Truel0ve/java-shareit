package ru.practicum.shareit.features.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.features.user.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find userShort
    Optional<UserShort> findUserById(Long id);

    // Patch user
    @Modifying
    @Query("UPDATE User u " +
            "SET u.name = ?2, u.email = ?3 " +
            "WHERE u.id = ?1")
    void patch(Long id,
               String name,
               String email);
}