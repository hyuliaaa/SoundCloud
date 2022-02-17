package com.example.soundcloud.model.DTO.user;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.POJO.Song;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class UserResponseDTO {

    private long id;
    private String username;
    private String email;
    private int age;
    private char gender;
    private LocalDateTime createdAt;
    private String profilePictureURL;
    private Set<SongWithoutUserDTO> uploadedSongs;

}
