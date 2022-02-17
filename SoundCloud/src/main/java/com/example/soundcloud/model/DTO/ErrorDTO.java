package com.example.soundcloud.model.DTO;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
public class ErrorDTO {

    private HttpStatus status;
    private String message;
    private LocalDateTime dateTime;

}
