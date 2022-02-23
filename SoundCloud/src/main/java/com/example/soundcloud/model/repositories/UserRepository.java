package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    //all inactive users whose last login was on this day over a month ago
    @Query(value = "SELECT * FROM users WHERE EXTRACT(DAY FROM last_active) = EXTRACT(DAY FROM current_timestamp) AND last_active < CURDATE()",
            nativeQuery = true)
    Set<User> getInactiveUsers();

}
