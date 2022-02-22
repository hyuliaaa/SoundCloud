package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController {

    @SneakyThrows
    @GetMapping("profile_pics/{filename}")
    public void downloadProfilePicture(@PathVariable String filename, HttpServletResponse response){
        File f = new File("profile_pictures" + File.separator + filename);
        if(!f.exists()){
            throw new NotFoundException("File doesn not exist!");
        }
        Files.copy(f.toPath(),response.getOutputStream());
    }

    @SneakyThrows
    @GetMapping("song_pictures/{filename}")
    public void downloadSongPicture(@PathVariable String filename, HttpServletResponse response){
        File f = new File("song_pictures" + File.separator + filename);
        if(!f.exists()){
            throw new NotFoundException("File does not exist!");
        }
        Files.copy(f.toPath(),response.getOutputStream());
    }

    @SneakyThrows
    @GetMapping("playlist_pictures/{filename}")
    public void downloadPlaylistPicture(@PathVariable String filename, HttpServletResponse response){
        File f = new File("playlist_pictures" + File.separator + filename);
        if(!f.exists()){
            throw new NotFoundException("File does not exist!");
        }
        Files.copy(f.toPath(),response.getOutputStream());
    }

    @SneakyThrows
    @GetMapping("songs/download-song/{filename}")
    public void downloadSong(@PathVariable String filename, HttpServletResponse response){
        File f = new File("songs" + File.separator + filename);
        if(!f.exists()){
            throw new NotFoundException("Song does not exist!");
        }
        Files.copy(f.toPath(),response.getOutputStream());
    }
}
