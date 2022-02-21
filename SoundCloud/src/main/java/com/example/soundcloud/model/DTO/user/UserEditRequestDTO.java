package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserEditRequestDTO {

    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[^\\s]{1,16}$", message = "Invalid username")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email address!")
    private String email;

    @Min(value = 14, message = "Sorry, you cannot set your age below 14!")
    private int age;

    @NotNull(message = "Gender cannot be null!")
    private Character gender;

}
