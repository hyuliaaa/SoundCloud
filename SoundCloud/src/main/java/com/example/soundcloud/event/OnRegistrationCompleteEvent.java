package com.example.soundcloud.event;

import com.example.soundcloud.model.entities.User;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;


@Getter
public class OnRegistrationCompleteEvent extends ApplicationEvent {

    private User user;

    public OnRegistrationCompleteEvent(User user) {
        super(user);

        this.user = user;
    }

}
