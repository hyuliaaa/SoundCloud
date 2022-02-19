package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.service.SongService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.util.Set;

import static com.example.soundcloud.controller.UserController.USER_ID;

@RestController
@Data
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping("/upload_song")
    ResponseEntity<SongWithoutUserDTO> upload(@Valid @RequestBody SongUploadRequestDTO uploadDTO, HttpSession session){
        long id = (long) session.getAttribute(USER_ID);
        return new ResponseEntity<>(songService.upload(id, uploadDTO), HttpStatus.CREATED);
    }

    @GetMapping("/users/{id}/songs")
    ResponseEntity<Set<SongWithoutUserDTO>> getAllUploadedByUserId(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.getAllUploaded((long) session.getAttribute(USER_ID), id));
    }

    @PostMapping("/songs/{id}/like")
    ResponseEntity<Integer>like(@PathVariable long id, HttpSession session){
        return new ResponseEntity<>(songService.like(id, (long) session.getAttribute(USER_ID)), HttpStatus.CREATED);
    }

    @DeleteMapping("/songs/{id}/unlike")
    ResponseEntity<Integer>unlike(@PathVariable long id, HttpSession session){
        return new ResponseEntity<>(songService.unlike(id, (long) session.getAttribute(USER_ID)), HttpStatus.NO_CONTENT);
    }

}
