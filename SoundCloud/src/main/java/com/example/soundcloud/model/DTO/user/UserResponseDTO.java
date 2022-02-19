package com.example.soundcloud.model.DTO.user;
import lombok.Data;

@Data
public class UserResponseDTO {

    private long id;
    private String username;
    private String email;
    private int age;
    private char gender;
    private String profilePictureURL;

}
