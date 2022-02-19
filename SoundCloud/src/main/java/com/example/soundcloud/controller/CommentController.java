package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.comment.CommentAddRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentEditRequestDTO;
import com.example.soundcloud.model.DTO.comment.CommentResponseDTO;
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


    //TODO figure out what to return
    @PostMapping("/comment")
    ResponseEntity<String> addComment(@Valid @RequestBody CommentAddRequestDTO requestDTO, HttpSession session){
        commentService.addComment((long)session.getAttribute(USER_ID), requestDTO);
        return new ResponseEntity<>("Comment was added successfully", HttpStatus.CREATED);
    }

    @GetMapping("/songs/{id}/comments")
    ResponseEntity<Set<CommentResponseDTO>> viewAllComments(@PathVariable(name = "id") long songId){
        return ResponseEntity.ok(commentService.getAll(songId));
    }

    @PostMapping("/comments/{id}/like")
    ResponseEntity<String> like(@PathVariable(name = "id") long commentId, HttpSession session){
        commentService.like((long) session.getAttribute(USER_ID), commentId);
        return ResponseEntity.ok("Comment was liked");
    }

    @DeleteMapping("/comments/{id}/like")
    ResponseEntity<String> unlike(@PathVariable(name = "id") long commentId, HttpSession session){
        commentService.unlike((long) session.getAttribute(USER_ID), commentId);
        return ResponseEntity.ok("Comment was unliked");
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
