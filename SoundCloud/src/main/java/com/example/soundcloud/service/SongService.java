package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.DTO.song.SongUploadDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.POJO.Song;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Data
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Utils utils;

    public SongWithoutUserDTO upload(long id, SongUploadDTO uploadDTO) {

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(utils.getUserById(id));
        song.setUploadedAt(LocalDateTime.now());
        //TODO validate song url

        songRepository.save(song);
        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    public Set<SongWithoutUserDTO> getAllUploaded (long id){

        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("no such user"))
                .getUploadedSongs().stream()
                .map((song) -> modelMapper.map(song, SongWithoutUserDTO.class))
                .sorted(Comparator.comparing(SongWithoutUserDTO::getUploadedAt))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    //TODO find by id or else throw -> in a separate method
    public int like(long songId, long userId) {

        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        song.getLikes().add(user);
        songRepository.save(song);
        return song.getLikes().size();
    }

}
