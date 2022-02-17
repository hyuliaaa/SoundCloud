package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRegisterRequestDTO {

    @NotBlank(message = "invalid username")
    private String username;

    @Email(message = "invalid email address")
    private String email;

    //upper case, lower case, number, alpha-numeric character, 8-16 symbols
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$",
    message = "weak password")
    private String password;

    //TODO: validate that passwords match
    private String confirmedPassword;

    @Positive(message = "invalid age")
    private int age;

    @NotNull
    private char gender;

}
