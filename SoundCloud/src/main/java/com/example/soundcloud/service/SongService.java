package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.DTO.song.SongUploadDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.POJO.Song;
import com.example.soundcloud.model.POJO.User;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import lombok.Data;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

    public SongWithoutUserDTO upload(long id, SongUploadDTO uploadDTO) {

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(userRepository.findById(id).orElseThrow(() -> new NotFoundException("no such user")));
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

        User user = (userRepository.findById(userId).orElseThrow(() -> new NotFoundException("no such user")));
        Song song = (songRepository.findById(songId).orElseThrow(() -> new NotFoundException("no such song")));

        song.getLikes().add(user);
        songRepository.save(song);
        return song.getLikes().size();
    }

}
