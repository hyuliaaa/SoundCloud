package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.MessageDTO;
import com.example.soundcloud.model.DTO.playlist.*;

import com.example.soundcloud.service.PlaylistService;
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

    @PostMapping("/playlists/{id}/like")
    ResponseEntity<PlaylistWithLikesDTO> like(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(playlistService.like(id,(long)session.getAttribute(USER_ID)));
    }

    @DeleteMapping("/playlists/{id}/unlike")
    ResponseEntity<PlaylistWithLikesDTO> unlike(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(playlistService.unlike(id,(long)session.getAttribute(USER_ID)));
    }

    @PostMapping("/playlists/add_song")
    ResponseEntity<PlaylistWithSongsDTO> addSong(@RequestParam long playlistId,@RequestParam long songId, HttpSession session){
        return ResponseEntity.ok(playlistService.addSong(playlistId,songId,(long)session.getAttribute(USER_ID)));
    }

    @DeleteMapping("/playlists/delete_song")
    ResponseEntity<PlaylistWithSongsDTO> deleteSong(@RequestParam long playlistId,@RequestParam long songId, HttpSession session){
        return ResponseEntity.ok(playlistService.deleteSong(playlistId,songId,(long)session.getAttribute(USER_ID)));
    }

    @GetMapping("playlists/{id}")
    ResponseEntity<PlaylistResponseDTO> getByid(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(playlistService.getByid(id,(long)session.getAttribute(USER_ID)));
    }

    @GetMapping("/playlists/find-by-title/{title}")
    public ResponseEntity<Set<PlaylistResponseDTO>> getPlaylistByTitle(@PathVariable String title){
        return ResponseEntity.ok(playlistService.getByTitle(title));
    }

    @GetMapping("/playlists/view-all-liked")
    ResponseEntity<Set<PlaylistResponseDTO>> getAllUserLikedPlaylists(HttpSession session){
        return ResponseEntity.ok(playlistService.getAllUserLikedPlaylists((long) session.getAttribute(USER_ID)));
    }


    @DeleteMapping("playlists/{id}")
    ResponseEntity<MessageDTO> delete(@PathVariable(name = "id") long id, HttpSession session) {
        playlistService.delete(id,(long) session.getAttribute(USER_ID));
        return ResponseEntity.ok(new MessageDTO("Playlist was deleted"));
    }

    @PutMapping("playlists/edit")
    public PlaylistResponseDTO edit(@Valid @RequestBody PlaylistEditDTO dto, HttpSession session){
        return playlistService.edit((long) session.getAttribute(USER_ID), dto);
    }

}

