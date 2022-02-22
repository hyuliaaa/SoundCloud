package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    Set<User> getAllByLastActiveBefore(LocalDateTime dateTime);
}
