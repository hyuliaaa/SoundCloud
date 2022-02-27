package com.example.soundcloud.model.DTO.song;

import com.example.soundcloud.model.DTO.user.ShortUserDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongWithLikesDTO {

    private long id;
    private String title;
    private ShortUserDTO owner;
    private LocalDateTime uploadedAt;
    private int views;
    private String songUrl;
    private String coverPhotoUrl;
    private boolean isPublic;
    private int numberOfLikes;
}
