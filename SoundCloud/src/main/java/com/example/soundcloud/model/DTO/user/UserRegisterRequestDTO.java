package com.example.soundcloud.model.DTO.user;

import lombok.Data;

import javax.validation.constraints.*;

@Data
public class UserRegisterRequestDTO {

    //no spaces, 1-16 symbols
    @NotBlank(message = "Username cannot be empty")
    @Pattern(regexp = "^[^\\s]{1,16}$", message = "Invalid username")
    private String username;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email")
    private String email;

    //upper case, lower case, number, alpha-numeric character, 8-16 symbols
    @NotBlank(message = "Password cannot be empty")
    @Pattern(regexp = "^(?=.*\\d)(?=.*[A-Z])(?=.*[a-z])(?=.*[^\\w\\d\\s:])([^\\s]){8,16}$",
    message = "Password is too weak")
    private String password;

    @NotBlank(message = "Confirmed password cannot be empty")
    private String confirmedPassword;

    @Min(value = 14, message = "Sorry, you don't meet SoundCloud's minimum age requirements")
    private int age;

    @NotNull(message = "Gender cannot be null!")
    private Character gender;

}
