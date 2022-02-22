package com.example.soundcloud.model.DTO.playlist;

import lombok.Data;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class PlaylistEditDTO {

        @NotNull(message = "Playlist id cannot be null!")
        @Positive(message = "Invalid playlist id!")
        private int id;

        @NotBlank(message = "Title cannot be blank")
        @Column
        private String title;

        @NotNull(message = "Privacy cannot be null")
        @Column
        private Boolean isPublic;


}
