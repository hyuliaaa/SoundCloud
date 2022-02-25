package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.entities.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findTagByName(String name);

    Set<Tag> findAllByNameIn(Set<String> names);

    @Query(value = "SELECT * FROM tags AS t\n" +
            "LEFT JOIN descriptions_have_tags AS dht ON(t.id = dht.tag_id)\n" +
            "WHERE dht.description_id IS NULL", nativeQuery = true)
    Set<Tag> getUnusedTags();
}
