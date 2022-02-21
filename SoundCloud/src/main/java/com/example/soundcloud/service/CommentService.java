package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.model.DTO.comment.CommentAddRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentEditRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentResponseDTO;
import com.example.soundcloud.model.DTO.comment.CommentWithLikesDTO;
import com.example.soundcloud.model.entities.Comment;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.CommentRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
public class CommentService {

    @Autowired
    private Utils utils;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private CommentRepository commentRepository;

    public CommentResponseDTO addComment(long userId, CommentAddRequestDTO requestDTO) {
        User user = utils.getUserById(userId);
        Song song = utils.getSongById(requestDTO.getSongId());

        Comment comment = Comment.builder()
                .user(user).song(song)
                .content(requestDTO.getContent())
                .postedAt(LocalDateTime.now()).build();

        if (requestDTO.getParentCommentId() != null){
            Comment parentComment = utils.getCommentById(requestDTO.getParentCommentId());
            if (song != parentComment.getSong()){
                throw new BadRequestException("Comment and parent comment songs do not match");
            }
            comment.setParentComment(parentComment);
        }
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponseDTO.class);
    }

    public Set<CommentResponseDTO> getAll(long id) {
        Song song = utils.getSongById(id);
        return song.getComments().stream()
                .map(comment ->modelMapper.map(comment, CommentResponseDTO.class))
                .sorted(Comparator.comparing(CommentResponseDTO::getPostedAt))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public CommentWithLikesDTO like(long userId, long commentId) {
        User user = utils.getUserById(userId);
        Comment comment =utils.getCommentById(commentId);
        if (comment.getLikes().contains(user)){
            throw new BadRequestException("You have already liked this comment");
        }
        comment.getLikes().add(user);
        commentRepository.save(comment);
        CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
        CommentWithLikesDTO dto1 = modelMapper.map(dto, CommentWithLikesDTO.class);
        dto1.setLikes(comment.getLikes().size());
        return dto1;
    }

    public CommentWithLikesDTO unlike(long userId, long commentId) {
        User user = utils.getUserById(userId);
        Comment comment =utils.getCommentById(commentId);
        if (!comment.getLikes().contains(user)){
            throw new BadRequestException("You haven't liked this comment");
        }
        comment.getLikes().remove(user);
        commentRepository.save(comment);
        CommentResponseDTO dto = modelMapper.map(comment, CommentResponseDTO.class);
        CommentWithLikesDTO dto1 = modelMapper.map(dto, CommentWithLikesDTO.class);
        dto1.setLikes(comment.getLikes().size());
        return dto1;
    }

    public CommentResponseDTO edit(long userId, long commentId, CommentEditRequestDTO requestDTO) {
        User user = utils.getUserById(userId);
        Comment comment = utils.getCommentById(commentId);
        if (!comment.getUser().equals(user)){
            throw new ForbiddenException("You cannot edit this comment");
        }

        comment.setContent(requestDTO.getContent());
        commentRepository.save(comment);
        return modelMapper.map(comment, CommentResponseDTO.class);
    }

    public void delete(long userId, long commentId) {
        User user = utils.getUserById(userId);
        Comment comment = utils.getCommentById(commentId);

        if (!comment.getUser().equals(user)){
            throw new ForbiddenException("You cannot delete this comment");
        }

        commentRepository.delete(comment);
    }
}
