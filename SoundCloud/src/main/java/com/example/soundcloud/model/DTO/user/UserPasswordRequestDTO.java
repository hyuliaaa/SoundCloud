package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserPasswordRequestDTO {

    @NotBlank(message = "Password cannot be empty")
    private String oldPassword;

    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$",
             message = "Password is too weak")
    private String newPassword;

    @NotBlank(message = "Confirmed password cannot be empty")
    private String confirmedPassword;
}
