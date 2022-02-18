package com.example.soundcloud.model.repositories;

import com.example.soundcloud.model.POJO.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
}
