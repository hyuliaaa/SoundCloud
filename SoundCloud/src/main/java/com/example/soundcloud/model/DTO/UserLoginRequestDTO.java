package com.example.soundcloud.model.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
public class UserLoginRequestDTO {

    @NotBlank(message = "username field cannot be empty")
    private String username;

    @NotBlank(message = "password field cannot be empty")
    private String password;

}
