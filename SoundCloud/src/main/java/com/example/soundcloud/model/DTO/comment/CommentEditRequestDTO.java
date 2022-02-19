package com.example.soundcloud.model.DTO.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CommentEditRequestDTO {

    @NotBlank(message = "You cannot leave an empty comment")
    private String content;
}
