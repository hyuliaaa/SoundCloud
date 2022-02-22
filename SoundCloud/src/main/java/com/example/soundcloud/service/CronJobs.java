package com.example.soundcloud.service;

import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Set;

@Component
public class CronJobs {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private EmailService emailService;

    @Scheduled(cron = "${cron.expression}")
    public void sendReEngagementEmail(){

        String subject = "Hey, there";
        String message = "We've missed you";

        //todo how to mark when someone has already been informed this month
        Set<User> inactiveUsers = userRepository.getAllByLastActiveBefore(LocalDateTime.now().minus(1, ChronoUnit.MINUTES));
        inactiveUsers.forEach(user -> emailService.sendSimpleMessage(user.getEmail(), subject, message));
    }
}
