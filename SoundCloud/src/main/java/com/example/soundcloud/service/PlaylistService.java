package com.example.soundcloud.service;

import com.example.soundcloud.model.DTO.playlist.PlaylistCreateRequestDTO;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.entities.Playlist;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.PlaylistRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.validation.Valid;
import java.time.LocalDateTime;


@Service
@Data
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Utils utils;

    public PlaylistResponseDTO createPlaylist(long id, @Valid PlaylistCreateRequestDTO createPlaylistDTO) {
        PlaylistResponseDTO dto = new PlaylistResponseDTO();
        Playlist playlist = new Playlist();
        modelMapper.map(createPlaylistDTO,playlist);
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setLastModified(LocalDateTime.now());
        playlist.setOwner(utils.getUserById(id));
        playlistRepository.save(playlist);
        modelMapper.map(playlist,dto);
        dto.setOwnerId(id);
        return dto;
    }
}
