package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UserEditRequestDTO {

    @NotBlank(message = "invalid username")
    private String username;

    @Email(message = "invalid email address")
    private String email;

    @Positive(message = "invalid age")
    private int age;

    private char gender;

    private String profilePictureURL;

}
