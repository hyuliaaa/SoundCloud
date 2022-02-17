package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.POJO.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

}
