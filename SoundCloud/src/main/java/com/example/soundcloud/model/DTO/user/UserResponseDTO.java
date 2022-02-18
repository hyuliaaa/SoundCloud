package com.example.soundcloud.model.DTO.user;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserResponseDTO {

    private long id;
    private String username;
    private String email;
    private int age;
    private char gender;
    private LocalDateTime createdAt;
    private String profilePictureURL;

}
