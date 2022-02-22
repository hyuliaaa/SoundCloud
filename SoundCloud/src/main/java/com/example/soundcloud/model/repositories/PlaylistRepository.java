package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

    Set<Playlist> findByTitleStartsWith(String title);
}
