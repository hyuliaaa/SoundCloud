package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlaylistRepository extends JpaRepository<Playlist,Long> {

}
