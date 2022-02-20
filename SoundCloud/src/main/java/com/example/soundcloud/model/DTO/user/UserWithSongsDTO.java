package com.example.soundcloud.model.DTO.user;

import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import lombok.Data;

import java.util.Set;

@Data
public class UserWithSongsDTO {
    private long id;
    private String username;
    private String email;
    private int age;
    private char gender;
    private String profilePictureURL;
    private Set<SongWithoutUserDTO> likedSongs;
}

