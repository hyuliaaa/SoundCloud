package com.example.soundcloud.event;

import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.service.EmailService;
import com.example.soundcloud.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent> {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event) {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event) {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        userService.createVerificationToken(user, token);

        String recipientAddress = user.getEmail();
        String subject = "Confirm registration";
        String confirmationUrl = "/confirm_registration?token=" + token;

        emailService.sendSimpleMessage(recipientAddress, subject, "Confirm your email here: http://localhost:9090" + confirmationUrl);
    }
}
