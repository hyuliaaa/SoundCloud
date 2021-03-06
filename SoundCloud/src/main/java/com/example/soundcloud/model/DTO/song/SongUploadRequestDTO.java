package com.example.soundcloud.model.DTO.song;

import com.example.soundcloud.model.DTO.description.DescriptionDTO;
import com.example.soundcloud.model.entities.Description;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@Data
public class SongUploadRequestDTO {

    @NotBlank(message = "Title cannot be empty")
    private String title;

    private Boolean isPublic;

    @Valid
    private DescriptionDTO description;
}
