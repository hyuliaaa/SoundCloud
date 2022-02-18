package com.example.soundcloud.model.DTO.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCommentRequestDTO {

    @NotNull(message = "Song id is necessary")
    private Long songId;

    @NotBlank(message = "Comment cannot be empty")
    private String content;

    private Long parentCommentId;
}
