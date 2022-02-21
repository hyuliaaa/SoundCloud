package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.playlist.PlaylistCreateRequestDTO;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.service.PlaylistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import static com.example.soundcloud.controller.UserController.USER_ID;

@RestController
public class PlaylistController {

    @Autowired
    private PlaylistService playlistService;

    @PostMapping("/create_playlist")
    ResponseEntity<PlaylistResponseDTO> createPlaylist(@Valid @RequestBody PlaylistCreateRequestDTO createPlaylistDTO, HttpSession session){
        long id = (long) session.getAttribute(USER_ID);
        return new ResponseEntity<>(playlistService.createPlaylist(id, createPlaylistDTO), HttpStatus.CREATED);
    }

    @PostMapping("/playlists/{playlist_id}/upload-playlist-image")
    public String uploadSongImage(@PathVariable("playlist_id") long playlistId, @RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return playlistService.uploadPlaylistPicture(playlistId,file, (long) session.getAttribute(USER_ID));
    }

}

