package com.example.soundcloud.controller;

import com.example.soundcloud.event.OnRegistrationCompleteEvent;
import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.DTO.user.*;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.service.EmailService;
import com.example.soundcloud.service.UserService;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.Set;

@RestController
@Data
public class UserController {

    public static final String LOGGED = "logged";
    public static final String IP = "IP";
    public static final String USER_ID = "user_id";

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    ApplicationEventPublisher eventPublisher;

    @Autowired
    ModelMapper modelMapper;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO, HttpServletRequest request){

        if (request.getSession().getAttribute(LOGGED) != null){
            throw new BadRequestException("You must log out in order to register again");
        }

        User user = userService.register(requestDTO);
        String appUrl = request.getContextPath();
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, request.getLocale(), appUrl));
        return new ResponseEntity<>(modelMapper.map(user, UserResponseDTO.class), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO, HttpServletRequest request){

        if(request.getSession().getAttribute(LOGGED) != null){
            throw new BadRequestException("You are already logged in");
        }

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

    @PostMapping("/users/upload")
    public String uploadProfileImage(@RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return userService.uploadPicture(file, (long) session.getAttribute(USER_ID));
    }


//    @DeleteMapping("delete-user/{id}")
//    public void deleteUser(@PathVariable long id){
//        UserResponseDTO user = userService.getById(id);
//        userService.deleteUser(user);
//        ResponseEntity.status(204);
//    }

    @PostMapping("/users/{id}/follow")
    public ResponseEntity<String> followUser(@PathVariable long id, HttpSession session) {
        userService.follow((long) session.getAttribute(USER_ID), id);
        return ResponseEntity.ok("Followed successfully");
    }

    @GetMapping("/users/{id}/following")
    public ResponseEntity<Set<UserResponseDTO>> getFollowing(@PathVariable long id) {
        return ResponseEntity.ok(userService.getFollowing(id));
    }

    @GetMapping("/users/{id}/followers")
    public ResponseEntity<Set<UserResponseDTO>> getFollowers(@PathVariable long id) {
        return ResponseEntity.ok(userService.getFollowers(id));
    }

    @PostMapping("/logout")
    public void logout(HttpSession session){
        session.invalidate();
    }

    @GetMapping("users/{id}/liked-songs")
    public ResponseEntity<Set<SongWithoutUserDTO>> getLikedSongs(@PathVariable long id){
        return ResponseEntity.ok(userService.getLikedSongs(id));
    }
}
