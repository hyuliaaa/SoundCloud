package com.example.soundcloud.model.DTO.playlist;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
public class PlaylistResponseDTO {


    private String title;
    private LocalDateTime createdAt;
    private LocalDateTime lastModified;
    private String coverPhotoUrl;
    private long ownerId;
    private boolean isPublic;
}
