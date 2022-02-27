package com.example.soundcloud.model.DTO.song;

import com.example.soundcloud.model.DTO.user.ShortUserDTO;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SongResponseDTO {

    private long id;
    private String title;
    private ShortUserDTO owner;
    private LocalDateTime uploadedAt;
    private int views;
    private String songUrl;
    private String coverPhotoUrl;
    private boolean isPublic;

}
