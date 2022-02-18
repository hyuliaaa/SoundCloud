package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.DTO.user.*;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Files;
import java.time.LocalDateTime;

@Service
@Data
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Utils utils;

    public UserResponseDTO register(UserRegisterRequestDTO requestDTO){
        if (userRepository.findByUsername(requestDTO.getUsername()).isPresent()){
            throw new BadRequestException("username already exists");
        }
        if (userRepository.findByEmail(requestDTO.getEmail()).isPresent()){
            throw new BadRequestException("email already exists");
        }

        User user = modelMapper.map(requestDTO, User.class);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now() );
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO login(UserLoginRequestDTO requestDTO){

        User user = userRepository.findByUsername(requestDTO.getUsername()).
                orElseThrow(() -> new BadRequestException("wrong credentials"));
        String password = requestDTO.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("wrong credentials");
        }
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO changePassword(long id, UserChangePasswordDTO dto){

        User user = utils.getUserById(id);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("wrong password");
        }
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO edit(long id, UserEditRequestDTO dto){
        User user = utils.getUserById(id);
        User userFromRequest = modelMapper.map(dto, User.class);
        //TODO make it work
        //BeanUtils.copyProperties(userFromRequest, user);
        // save user
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO getById(long id) {
        User user = utils.getUserById(id);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    @SneakyThrows
    public String uploadPicture(MultipartFile file, long id) {
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        Files.copy(file.getInputStream(), new File("profile_pictures" + File.separator + name).toPath());
        User user = userRepository.getById(id);
        user.setProfilePictureUrl(name);
        userRepository.save(user);
        return name;
    }
}