package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithLikesDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import com.example.soundcloud.service.SongService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    ResponseEntity<SongWithLikesDTO>like(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.like(id,(long)session.getAttribute(USER_ID)));
    }

    @DeleteMapping("/songs/{id}/unlike")
    ResponseEntity<SongWithLikesDTO>unlike(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.unlike(id, (long) session.getAttribute(USER_ID)));
    }

    @GetMapping("/songs/{title}")
    public ResponseEntity<SongWithoutUserDTO> getSongByTitle(@PathVariable String title){
        return ResponseEntity.ok(songService.getByTitle(title));
    }

    @PostMapping("/songs/{song_id}/upload-song-image")
    public String uploadSongImage(@PathVariable long song_id,@RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return songService.uploadSongPicture(song_id,file, (long) session.getAttribute(USER_ID));
    }
    //edit song
}
