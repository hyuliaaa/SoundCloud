package com.example.soundcloud.model.DTO.playlist;

import com.example.soundcloud.model.DTO.song.SongResponseDTO;
import com.example.soundcloud.model.entities.Song;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PlaylistWithSongsInfo {
    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String coverPhotoUrl;
    private long ownerId;
    private boolean isPublic;
    private Set<SongResponseDTO> songs;
}
