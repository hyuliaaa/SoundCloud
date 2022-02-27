package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.Song;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    Set <Song> findByTitleStartsWith(String title);
    List <Song> findByOrderByLikesAsc();
    Optional <Song> findBySongUrl(String songUrl);

}
