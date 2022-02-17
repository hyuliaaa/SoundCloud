package com.example.soundcloud.model.DTO.song;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SongWithoutUserDTO {

    private long id;
    private String title;
    private LocalDateTime uploadedAt;
    private int views;
    private String songUrl;
    private String coverPhotoUrl;
    private boolean isPublic;

}
