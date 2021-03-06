package com.example.soundcloud.controller;

import com.example.soundcloud.event.OnRegistrationCompleteEvent;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.model.DTO.MessageDTO;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.DTO.song.SongResponseDTO;
import com.example.soundcloud.model.DTO.user.*;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.service.EmailService;
import com.example.soundcloud.service.UserService;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
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

    @Autowired
    Utils utils;

    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRegisterRequestDTO requestDTO, HttpServletRequest request){

        if (request.getSession().getAttribute(LOGGED) != null){
            throw new ForbiddenException("You must log out in order to register again");
        }

        User user = userService.register(requestDTO);
        eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
        return new ResponseEntity<>(modelMapper.map(user, UserResponseDTO.class), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<UserResponseDTO> login(@Valid @RequestBody UserLoginRequestDTO requestDTO, HttpServletRequest request){

        if(request.getSession().getAttribute(LOGGED) != null){
            throw new ForbiddenException("You are already logged in");
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

    @PutMapping("/users")
    public ResponseEntity<UserResponseDTO> editUser(@Valid @RequestBody UserEditRequestDTO dto, HttpSession session){
        Long id = (long) session.getAttribute(USER_ID);
        UserResponseDTO responseDTO = userService.edit(id, dto);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable long id) {
        return ResponseEntity.ok(userService.getById(id));
    }

    @GetMapping("/find-by-username/{username}/{offset}/{pageSize}")
    public ResponseEntity<Page<UserResponseDTO>> getUserByUsername(@PathVariable String username, @PathVariable int offset, @PathVariable int pageSize){
        return ResponseEntity.ok(userService.getByUsername(offset,pageSize,username));
    }

    @PostMapping("/users/upload")
    public MessageDTO uploadProfileImage(@RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return userService.uploadPicture(file, (long) session.getAttribute(USER_ID));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<UserResponseDTO> deleteUser(@PathVariable long id,HttpSession session){
        UserResponseDTO userResponseDTO = userService.deleteUser(id,(long) session.getAttribute(USER_ID));
        session.invalidate();
        return ResponseEntity.ok(userResponseDTO);
    }

    @PostMapping("/users/{id}/follow")
    public ResponseEntity<UserWithFollowersDTO> followUser(@PathVariable long id, HttpSession session) {
        UserWithFollowersDTO responseDTO = userService.follow((long) session.getAttribute(USER_ID), id);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/users/{id}/follow")
    public ResponseEntity<UserWithFollowersDTO> unfollowUser(@PathVariable long id, HttpSession session) {
        UserWithFollowersDTO responseDTO = userService.unfollow((long) session.getAttribute(USER_ID), id);
        return ResponseEntity.ok(responseDTO);
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

    @GetMapping("/users/{id}/liked-songs/{offset}/{pageSize}")
    public ResponseEntity<Page<SongResponseDTO>> getLikedSongs(@PathVariable long id, @PathVariable int offset, @PathVariable int pageSize){
        return ResponseEntity.ok(userService.getLikedSongs(offset, pageSize, id));
    }

    @GetMapping("users/{id}/liked-playlists")
    public ResponseEntity<Set<PlaylistResponseDTO>> getLikedPlaylists(@PathVariable long id){
        return ResponseEntity.ok(userService.getLikedPlaylists(id));
    }

    @PutMapping("/reset_password")
    public ResponseEntity<MessageDTO> resetPassword(@Valid @RequestBody ResetPasswordDTO dto, HttpSession session){
        if (session.getAttribute(LOGGED) != null){
            throw new ForbiddenException("You cannot reset your email when you are logged in");
        }
        userService.resetPassword(dto);
        return ResponseEntity.ok(new MessageDTO("A temporary password has been sent to your email address"));
    }
}
