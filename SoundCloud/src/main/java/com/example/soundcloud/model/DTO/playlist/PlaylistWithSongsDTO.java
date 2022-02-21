package com.example.soundcloud.model.DTO.playlist;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PlaylistWithSongsDTO {
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String coverPhotoUrl;
    private long ownerId;
    private boolean isPublic;
    private int numberOfSongs;
}
