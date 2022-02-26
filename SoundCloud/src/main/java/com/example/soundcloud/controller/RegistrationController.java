package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.MessageDTO;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.entities.VerificationToken;
import com.example.soundcloud.model.repositories.VerificationTokenRepository;
import com.example.soundcloud.service.UserService;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.transaction.Transactional;

@RestController
@Data
public class RegistrationController {

    @Autowired
    private UserService userService;

    @Autowired
    private VerificationTokenRepository tokenRepository;

    @Transactional
    @GetMapping("/confirm_registration")
    public ResponseEntity<MessageDTO> confirmRegistration(@RequestParam("token") String token) {

        VerificationToken verificationToken = userService.getVerificationToken(token);
        if (verificationToken == null) {
            throw new BadRequestException("Invalid token!");
        }
        User user = verificationToken.getUser();

        user.setEnabled(true);
        userService.saveRegisteredUser(user);
        tokenRepository.delete(verificationToken);
        return ResponseEntity.ok(new MessageDTO("Email confirmed successfully!"));
    }
}
