package com.example.soundcloud.model.DTO.playlist;

import com.example.soundcloud.model.entities.User;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
public class PlaylistCreateRequestDTO {

    @Column
    private String title;

    @Column
    private boolean isPublic;

}
