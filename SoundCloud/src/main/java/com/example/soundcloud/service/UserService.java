package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.DTO.user.*;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.entities.VerificationToken;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.model.repositories.VerificationTokenRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.modelmapper.ModelMapper;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.io.FilenameUtils;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;
import static org.passay.DigestDictionaryRule.ERROR_CODE;


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

    @Autowired
    private EmailService emailService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    public User register(UserRegisterRequestDTO requestDTO){
        String username = requestDTO.getUsername();
        String email = requestDTO.getEmail();

        if (userRepository.findByUsername(username).isPresent()){
            throw new BadRequestException("Username already exists!");
        }
        if (userRepository.findByEmail(email).isPresent()){
            throw new BadRequestException("Email already exists!");
        }

        String password = requestDTO.getPassword();
        String confirmedPassword = requestDTO.getConfirmedPassword();

        if(!password.equals(confirmedPassword)){
            throw new BadRequestException("Passwords do not match!");
        }

        User user = modelMapper.map(requestDTO, User.class);
        user.setPassword(passwordEncoder.encode(requestDTO.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        user.setLastActive(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    public UserResponseDTO login(UserLoginRequestDTO requestDTO){

        User user = userRepository.findByUsername(requestDTO.getUsername()).
                orElseThrow(() -> new BadRequestException("Wrong credentials!"));
        String password = requestDTO.getPassword();
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadRequestException("Wrong credentials!");
        }
        user.setLastActive(LocalDateTime.now());
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO changePassword(long id, UserPasswordRequestDTO dto){

        User user = utils.getUserById(id);
        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Wrong password!");
        }

        if(passwordEncoder.matches(dto.getNewPassword(),user.getPassword())){
            throw new BadRequestException("Your new password cannot be the same as your old one!");
        }

        String password = dto.getNewPassword();
        String confirmedPassword = dto.getConfirmedPassword();
        if(!password.equals(confirmedPassword)){
            throw new BadRequestException("Passwords do not match!");
        }

        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);

        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO edit(long id, UserEditRequestDTO dto){

        User user = utils.getUserById(id);
        if (!user.getUsername().equals(dto.getUsername()) && userRepository.findByUsername(dto.getUsername()).isPresent()){
            throw new BadRequestException("Username already exists!");
        }
        if (!user.getEmail().equals(dto.getEmail()) && userRepository.findByEmail(dto.getEmail()).isPresent()){
            throw new BadRequestException("Email already exists!");
        }

        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setGender(dto.getGender());
        userRepository.save(user);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public UserResponseDTO getById(long id) {
        User user = utils.getUserById(id);
        return modelMapper.map(user, UserResponseDTO.class);
    }

    public Page<UserResponseDTO> getByUsername(int offset, int pageSize, String username){
        return utils.getUserByUsername(offset,pageSize,username);
    }

    @SneakyThrows
    public String uploadPicture(MultipartFile file, long id) {
        User user = userRepository.getById(id);
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("profile_pictures" + File.separator + name);
        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        user.setProfilePictureUrl(name);
        userRepository.save(user);
        return f.getName();
    }
    public UserResponseDTO deleteUser(UserResponseDTO user) {
        User u = modelMapper.map(user,User.class);
        userRepository.delete(u);
        return user;
    }


    public void follow(long id, long otherUser) {
        if (id == otherUser){
            throw new BadRequestException("You cannot follow yourself");
        }

        User user = utils.getUserById(id);
        User other = utils.getUserById(otherUser);
        if (!user.getFollowing().add(other)){
            throw new BadRequestException("You are already following this user");
        }
        userRepository.save(user);
    }

    public Set<UserResponseDTO> getFollowing(long id) {
        User user = utils.getUserById(id);
        return user.getFollowing().stream().map((user1 -> modelMapper.map(user1, UserResponseDTO.class))).collect(Collectors.toSet());
    }

    public Set<UserResponseDTO> getFollowers(long id) {
        User user = utils.getUserById(id);
        return user.getFollowers().stream().map((user1 -> modelMapper.map(user1, UserResponseDTO.class))).collect(Collectors.toSet());
    }

    public Page<SongWithoutUserDTO> getLikedSongs(int offset, int pageSize,long id) {
        User user = utils.getUserById(id);
        return utils.getLikedSongs(offset,pageSize,id);

//        return user.getLikedSongs().stream().map((song -> modelMapper.map(song, SongWithoutUserDTO.class))).collect(Collectors.toSet());
    }

    public void createVerificationToken(User user, String token) {
        VerificationToken verificationToken = new VerificationToken();
        verificationToken.setUser(user);
        verificationToken.setToken(token);

        tokenRepository.save(verificationToken);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    public VerificationToken getVerificationToken(String token) {
        return tokenRepository.findByToken(token);
    }

    public void resetPassword(ResetPasswordDTO dto){

        User user = utils.getUserByEmail(dto.getEmail());
        String password = generatePassayPassword();
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        String message = "Your new password is: " + password;
        emailService.sendSimpleMessage(user.getEmail(), "Reset password", message);
    }

    private String generatePassayPassword() {
        PasswordGenerator gen = new PasswordGenerator();
        org.passay.CharacterData lowerCaseChars = EnglishCharacterData.LowerCase;
        CharacterRule lowerCaseRule = new CharacterRule(lowerCaseChars);
        lowerCaseRule.setNumberOfCharacters(2);

        org.passay.CharacterData upperCaseChars = EnglishCharacterData.UpperCase;
        CharacterRule upperCaseRule = new CharacterRule(upperCaseChars);
        upperCaseRule.setNumberOfCharacters(2);

        org.passay.CharacterData digitChars = EnglishCharacterData.Digit;
        CharacterRule digitRule = new CharacterRule(digitChars);
        digitRule.setNumberOfCharacters(2);

        org.passay.CharacterData specialChars = new org.passay.CharacterData() {
            public String getErrorCode() {
                return ERROR_CODE;
            }

            public String getCharacters() {
                return "!@#$%^&*()_+";
            }
        };
        CharacterRule splCharRule = new CharacterRule(specialChars);
        splCharRule.setNumberOfCharacters(2);

        String password = gen.generatePassword(10, splCharRule, lowerCaseRule,
                upperCaseRule, digitRule);
        return password;
    }

    public Set<PlaylistResponseDTO> getLikedPlaylists(long id) {
        User user = utils.getUserById(id);
        return user.getLikedPlaylists().stream().map(playlist -> modelMapper.map(playlist,PlaylistResponseDTO.class)).collect(Collectors.toSet());
    }
}
