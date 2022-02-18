package com.example.soundcloud.model.DTO.comment;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class AddCommentRequestDTO {

    @NotNull(message = "song id is necessary")
    private long songId;

    @NotBlank(message = "comment cannot be empty")
    private String content;

    private Long parentCommentId;
}
