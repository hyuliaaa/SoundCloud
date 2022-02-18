package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.song.SongUploadDTO;
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

    @PostMapping("/upload")
    ResponseEntity<SongWithoutUserDTO> upload(@Valid @RequestBody SongUploadDTO uploadDTO, HttpSession session){
        long id = (long) session.getAttribute(USER_ID);
        return new ResponseEntity<>(songService.upload(id, uploadDTO), HttpStatus.CREATED);
    }

    @GetMapping("/{id}/songs")
    ResponseEntity<Set<SongWithoutUserDTO>> getAllUploadedByUserId(@PathVariable long id){
        return ResponseEntity.ok(songService.getAllUploaded(id));
    }

    @PostMapping("/{id}/like")
    ResponseEntity<Integer>like(@PathVariable long id, HttpSession session){
        return new ResponseEntity<>(songService.like(id, (long) session.getAttribute(USER_ID)), HttpStatus.CREATED);
    }

}