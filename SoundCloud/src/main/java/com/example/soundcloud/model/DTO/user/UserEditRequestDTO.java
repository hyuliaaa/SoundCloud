package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserEditRequestDTO {

    @NotBlank(message = "Invalid username!")
    private String username;

    @Email(message = "Invalid email address!")
    private String email;

    @Min(value = 14, message = "Sorry, you cannot set your age below 14!")
    private int age;

    @NotNull(message = "Gender cannot be null!")
    private Character gender;

    private String profilePictureURL;

}
