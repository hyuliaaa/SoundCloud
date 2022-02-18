package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.comment.AddCommentRequestDTO;
import com.example.soundcloud.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;


import static com.example.soundcloud.controller.UserController.USER_ID;

@RestController
public class CommentController {

    @Autowired
    private CommentService commentService;


    //TODO figure out what to return
    @PostMapping("/comment")
    ResponseEntity<String> addComment(@Valid @RequestBody AddCommentRequestDTO requestDTO, HttpSession session){
        commentService.addComment((long)session.getAttribute(USER_ID), requestDTO);
        return new ResponseEntity<>("comment added successfully", HttpStatus.CREATED);
    }

}
