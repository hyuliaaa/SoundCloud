package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.comment.AddCommentRequestDTO;
import com.example.soundcloud.model.POJO.Comment;
import com.example.soundcloud.model.POJO.Song;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.CommentRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@Data
public class CommentService {

    @Autowired
    private Utils utils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentRepository commentRepository;

    public void addComment(long userId, AddCommentRequestDTO requestDTO) {
        User user = utils.getUserById(userId);
        Song song = getUtils().getSongById(requestDTO.getSongId());

        Comment comment = Comment.builder()
                .user(user).song(song)
                .content(requestDTO.getContent())
                .postedAt(LocalDateTime.now()).build();
        if (requestDTO.getParentCommentId() != null){
            Comment parentComment = utils.getCommentById(requestDTO.getParentCommentId());
            if (song != parentComment.getSong()){
                throw new BadRequestException("comment and parent comment songs do not match");
            }
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
    }
}
