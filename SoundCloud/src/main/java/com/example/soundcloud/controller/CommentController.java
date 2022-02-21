package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.comment.CommentAddRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentEditRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentResponseDTO;
import com.example.soundcloud.model.DTO.comment.CommentWithLikesDTO;
import com.example.soundcloud.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import java.util.Set;

import static com.example.soundcloud.controller.UserController.USER_ID;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;

    @PostMapping("/comment")
    ResponseEntity<CommentResponseDTO> addComment(@Valid @RequestBody CommentAddRequestDTO requestDTO, HttpSession session){
        CommentResponseDTO responseDTO = commentService.addComment((long)session.getAttribute(USER_ID), requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @GetMapping("/songs/{id}/comments")
    ResponseEntity<Set<CommentResponseDTO>> viewAllComments(@PathVariable(name = "id") long songId){
        return ResponseEntity.ok(commentService.getAll(songId));
    }

    @PostMapping("/comments/{id}/like")
    ResponseEntity<CommentWithLikesDTO> like(@PathVariable(name = "id") long commentId, HttpSession session){
        CommentWithLikesDTO dto = commentService.like((long) session.getAttribute(USER_ID), commentId);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/comments/{id}/like")
    ResponseEntity<CommentWithLikesDTO> unlike(@PathVariable(name = "id") long commentId, HttpSession session){
        CommentWithLikesDTO dto = commentService.unlike((long) session.getAttribute(USER_ID), commentId);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/comments/{id}/edit")
    ResponseEntity<CommentResponseDTO> edit(@Valid @RequestBody CommentEditRequestDTO requestDTO, @PathVariable(name = "id") long commentId, HttpSession session){
        CommentResponseDTO responseDTO = commentService.edit((long) session.getAttribute(USER_ID), commentId, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/comments/{id}")
    ResponseEntity<String> delete(@PathVariable(name = "id") long commentId, HttpSession session){
        commentService.delete((long) session.getAttribute(USER_ID), commentId);
        return ResponseEntity.ok("Comment was deleted");
    }

}
