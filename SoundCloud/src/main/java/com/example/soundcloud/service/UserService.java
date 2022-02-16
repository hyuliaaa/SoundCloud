package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.UserLoginRequestDTO;
import com.example.soundcloud.model.DTO.UserRegisterRequestDTO;
import com.example.soundcloud.model.DTO.UserResponseDTO;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.UserRepository;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Data
@NoArgsConstructor
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

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
                orElseThrow(() -> new BadRequestException("wrong username"));
        String password = requestDTO.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("wrong password");
        }
        return modelMapper.map(user, UserResponseDTO.class);
    }
}
