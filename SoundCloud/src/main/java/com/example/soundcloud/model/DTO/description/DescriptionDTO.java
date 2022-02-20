package com.example.soundcloud.model.DTO.description;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class DescriptionDTO {

    @NotBlank(message = "Description cannot be blank")
    private String content;
}
