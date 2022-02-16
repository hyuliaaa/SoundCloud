package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.UserLoginRequestDTO;
import com.example.soundcloud.model.DTO.UserRegisterRequestDTO;
import com.example.soundcloud.model.DTO.UserResponseDTO;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.service.UserService;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Data
@NoArgsConstructor
public class UserController {

    public static final String LOGGED = "logged";
    public static final String IP = "IP";
    public static final String USER_ID = "user_id";

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO){

        UserResponseDTO responseDTO = userService.register(requestDTO);
        return new ResponseEntity<>(responseDTO, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO, HttpSession session, HttpServletRequest request){

        UserResponseDTO responseDTO = userService.login(requestDTO);
        session.setAttribute(LOGGED, true);
        session.setAttribute(IP, request.getRemoteAddr());

        //is it needed
        session.setAttribute(USER_ID, responseDTO.getId());
        return ResponseEntity.ok(responseDTO);
    }
}
