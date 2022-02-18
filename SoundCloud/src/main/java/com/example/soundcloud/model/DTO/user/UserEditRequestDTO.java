package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UserEditRequestDTO {

    @NotBlank(message = "Invalid username")
    private String username;

    @Email(message = "Invalid email address")
    private String email;

    @Positive(message = "Invalid age")
    private int age;

    @NotNull(message = "Gender cannot be null")
    private char gender;

    private String profilePictureURL;

}
