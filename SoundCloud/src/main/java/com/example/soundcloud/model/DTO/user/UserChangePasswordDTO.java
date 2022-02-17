package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class UserChangePasswordDTO {

    @NotBlank(message = "invalid password")
    private String oldPassword;

    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$",
            message = "weak password")
    private String newPassword;

    //TODO check if they match
    private String confirmedPassword;
}
