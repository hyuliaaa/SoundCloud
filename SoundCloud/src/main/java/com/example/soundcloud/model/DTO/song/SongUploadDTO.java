package com.example.soundcloud.model.DTO.song;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SongUploadDTO {

    @NotBlank
    private String title;

    @NotBlank
    private String songUrl;

    private String coverPhotoUrl;

    @NotNull
    private boolean isPublic;

    //TODO add description
}
