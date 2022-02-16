package com.example.soundcloud.model.DTO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponseDTO {

    private long id;
    private String username;
    private String email;
    private int age;
    private char gender;
    private LocalDateTime createdAt;
    private String profilePictureURL;
}
