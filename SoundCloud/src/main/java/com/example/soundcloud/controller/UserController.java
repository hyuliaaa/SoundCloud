package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.user.*;
import com.example.soundcloud.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@RestController
@Data
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
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO, HttpServletRequest request){

        UserResponseDTO responseDTO = userService.login(requestDTO);
        HttpSession session = request.getSession();
        session.setAttribute(LOGGED, true);
        session.setAttribute(IP, request.getRemoteAddr());
        session.setAttribute(USER_ID, responseDTO.getId());
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/password")
    public ResponseEntity<UserResponseDTO> editPassword(@Valid @RequestBody UserPasswordRequestDTO dto, HttpSession session){
        Long id = (long) session.getAttribute(USER_ID);
        UserResponseDTO responseDTO = userService.changePassword(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("/edit")
    public ResponseEntity<UserResponseDTO> editUser(@Valid @RequestBody UserEditRequestDTO dto, HttpSession session){
        Long id = (long) session.getAttribute(USER_ID);
        UserResponseDTO responseDTO = userService.edit(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/find-by-username/{username}")
    public ResponseEntity<UserResponseDTO> getUserByUsername(@PathVariable String username){
        return ResponseEntity.ok(userService.getByUsername(username));
    }

    @PostMapping("/profile_picture")
    public String uploadProfileImage(@RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return userService.uploadPicture(file, (long) session.getAttribute(USER_ID));
    }

    @PostMapping("/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }
}
