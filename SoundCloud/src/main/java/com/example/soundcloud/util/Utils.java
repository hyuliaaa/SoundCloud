package com.example.soundcloud.util;

import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.entities.Comment;
import com.example.soundcloud.model.entities.Playlist;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.CommentRepository;
import com.example.soundcloud.model.repositories.PlaylistRepository;
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

    @Autowired
    private PlaylistRepository playlistRepository;

    public User getUserById(long id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public User getUserByUsername(String username){
        return userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("User not found!"));
    }

    public Song getSongById(long id){
        return songRepository.findById(id).orElseThrow(() -> new NotFoundException("Song not found!"));
    }

    public Comment getCommentById(long id){
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found!"));
    }

    public Playlist getPlaylistById(long id){
       return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist not found!"));
    }

    public Song getSongByTitle(String title){
        return songRepository.findByTitle(title).orElseThrow(() -> new NotFoundException("Song not found!"));
    }
}