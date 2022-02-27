package com.example.soundcloud.model.DTO.song;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SongSearchDTO {

    private String title;
    private LocalDate after;
    private LocalDate before;
    private String tag;

}
