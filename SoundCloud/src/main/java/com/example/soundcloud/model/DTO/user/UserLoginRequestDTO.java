package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class UserLoginRequestDTO {

    @NotBlank(message = "username field cannot be empty")
    private String username;

    @NotBlank(message = "password field cannot be empty")
    private String password;

}
