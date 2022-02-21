package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
public class ResetPasswordDTO {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    private String email;
}
