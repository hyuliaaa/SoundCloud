package com.example.soundcloud.model.DTO.song;

import com.example.soundcloud.model.DTO.description.DescriptionDTO;
import com.example.soundcloud.model.entities.Description;
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

    private DescriptionDTO description;
}
