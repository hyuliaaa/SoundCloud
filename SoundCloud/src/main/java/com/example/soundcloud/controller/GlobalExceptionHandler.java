package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.exceptions.UnauthorizedException;
import com.example.soundcloud.model.DTO.ErrorDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ErrorDTO> handleUnauthorized(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.UNAUTHORIZED);
        errorDTO.setMessage(e.getMessage());
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorDTO> handleBadRequest(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.BAD_REQUEST);
        errorDTO.setMessage(e.getMessage());
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleMethodArgumentNotValid(MethodArgumentNotValidException e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.BAD_REQUEST);

        List<String> errorMessages = e.getFieldErrors()
                .stream().map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.toList());

        errorDTO.setMessage(String.join(", ", errorMessages));
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorDTO> handleNotFound(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.NOT_FOUND);
        errorDTO.setMessage(e.getMessage());
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ResponseEntity<ErrorDTO> handleForbidden(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.FORBIDDEN);
        errorDTO.setMessage(e.getMessage());
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleOtherExceptions(Exception e){
        ErrorDTO errorDTO = new ErrorDTO();
        errorDTO.setStatus(HttpStatus.INTERNAL_SERVER_ERROR);
        errorDTO.setMessage("Unexpected error: " + e.getMessage());
        e.printStackTrace();
        errorDTO.setDateTime(LocalDateTime.now());
        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
