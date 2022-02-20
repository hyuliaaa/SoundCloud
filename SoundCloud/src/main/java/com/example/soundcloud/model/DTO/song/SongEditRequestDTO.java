package com.example.soundcloud.model.DTO.song;

import com.example.soundcloud.model.DTO.description.DescriptionDTO;
import com.example.soundcloud.model.entities.Description;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SongEditRequestDTO {

    @Min(value = 1, message = "Invalid song id")
    private int id;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotNull
    private Boolean isPublic;

    @Valid
    private DescriptionDTO description;
}
