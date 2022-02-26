package com.example.soundcloud.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ErrorDTO {

    private HttpStatus status;
    private String message;
    private LocalDateTime dateTime;

}
