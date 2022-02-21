package com.example.soundcloud.model.DTO.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class CommentAddRequestDTO {

    @NotNull(message = "Song id cannot be null")
    @Positive(message = "Invalid song id")
    private Long songId;

    @NotBlank(message = "You cannot leave an empty comment")
    private String content;

    private Long parentCommentId;
}
