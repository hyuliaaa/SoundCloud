package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
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

    public SongWithoutUserDTO upload(long id, SongUploadRequestDTO uploadDTO) {

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(utils.getUserById(id));
        song.setUploadedAt(LocalDateTime.now());
        //TODO validate song url

        songRepository.save(song);
        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    public Set<SongWithoutUserDTO> getAllUploaded (long id, long otherUserId){

        Set<SongWithoutUserDTO> songs = utils.getUserById(id)
                .getUploadedSongs().stream()
                .map((song) -> modelMapper.map(song, SongWithoutUserDTO.class))
                .collect(Collectors.toSet());

        if (id != otherUserId)
            songs = songs.stream().filter(SongWithoutUserDTO::isPublic).collect(Collectors.toSet());

        return songs.stream().sorted(Comparator.comparing(SongWithoutUserDTO::getUploadedAt))
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    public int like(long songId, long userId) {

        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if (!song.isPublic() && song.getOwner() != user)
            throw new BadRequestException("Song is private");

        song.getLikes().add(user);
        songRepository.save(song);
        return song.getLikes().size();
    }

}
