package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Objects;

import static com.example.soundcloud.controller.UserController.IP;
import static com.example.soundcloud.controller.UserController.LOGGED;

@Component
public class LoggedInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (Objects.equals(request.getServletPath(), "/register")
            || Objects.equals(request.getServletPath(), "/login")) {
            return true;
        }
        HttpSession session = request.getSession();
        if (session.isNew()
            || session.getAttribute(LOGGED) == null
            || !(boolean) session.getAttribute(LOGGED)
            || !request.getRemoteAddr().equals(session.getAttribute(IP)))
        {
            throw new UnauthorizedException("you are not logged in");
        }

        return true;
    }
}
