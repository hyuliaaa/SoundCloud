package com.example.soundcloud.service;

import com.example.soundcloud.model.repositories.PlaylistRepository;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Data
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;
}
