package com.example.soundcloud.util;

import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.POJO.Comment;
import com.example.soundcloud.model.POJO.Song;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.CommentRepository;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Utils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private CommentRepository commentRepository;

    public User getUserById(long id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("user not found"));
    }

    public Song getSongById(long id){
        return songRepository.findById(id).orElseThrow(() -> new NotFoundException("song not found"));
    }

    public Comment getCommentById(long id){
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("comment not found"));
    }
}