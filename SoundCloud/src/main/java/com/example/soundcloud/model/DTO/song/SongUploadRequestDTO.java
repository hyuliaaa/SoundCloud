package com.example.soundcloud.model.DTO.song;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class SongUploadRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    @NotBlank(message = "Song URL cannot be empty")
    private String songUrl;

    private String coverPhotoUrl;

    private Boolean isPublic;

    //TODO add description
}
