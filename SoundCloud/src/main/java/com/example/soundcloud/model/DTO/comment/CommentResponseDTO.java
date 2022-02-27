package com.example.soundcloud.model.DTO.comment;

import com.example.soundcloud.model.DTO.user.ShortUserDTO;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentResponseDTO {

    private long id;
    private ShortUserDTO user;
    private String content;
    private LocalDateTime postedAt;

}
