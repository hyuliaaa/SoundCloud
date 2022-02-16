package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.UnauthorizedException;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.websocket.Endpoint;

import static com.example.soundcloud.controller.UserController.IP;
import static com.example.soundcloud.controller.UserController.LOGGED;

@Component
public class Interceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        if (handler instanceof )
//        HttpSession session = request.getSession();
//        if (session.isNew()
//                || !(boolean) session.getAttribute(LOGGED)
//                || request.getRemoteAddr() != session.getAttribute(IP))
//        {
//            throw new UnauthorizedException("you are not logged in");
//        }
        return true;
    }
}
