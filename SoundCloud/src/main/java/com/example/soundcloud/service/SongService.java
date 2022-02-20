package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.description.DescriptionDTO;
import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.entities.Description;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.Tag;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.DescriptionRepository;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.TagRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Data
public class SongService {

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DescriptionRepository descriptionRepository;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private Utils utils;

    @Transactional
    public SongWithoutUserDTO upload(long id, SongUploadRequestDTO uploadDTO) {

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(utils.getUserById(id));
        song.setUploadedAt(LocalDateTime.now());
        //TODO validate song url

        DescriptionDTO description = uploadDTO.getDescription();
        if (description != null){
            descriptionRepository.save(modelMapper.map(uploadDTO.getDescription(), Description.class));
            Set<Tag> tags = getTags(description.getContent());
            tagRepository.saveAll(tags);
        }

        songRepository.save(song);
        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    private Set<Tag> getTags(String content) {

        Pattern MY_PATTERN = Pattern.compile("#(\\S+)");
        Matcher matcher = MY_PATTERN.matcher(content);
        List<String> tags = new ArrayList();
        while (matcher.find()) {
            tags.add(matcher.group(1));
        }

        return tags.stream().map(Tag::new).collect(Collectors.toSet());
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

        if(user.getLikedSongs().contains(song)){
            throw new BadRequestException("User already liked this song!");
        }

        song.getLikes().add(user);
        songRepository.save(song);
        return song.getLikes().size();
    }


    public int unlike(long songId, long userId) {

        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if (!song.isPublic() && song.getOwner() != user)
            throw new BadRequestException("Song is private");

        if(!user.getLikedSongs().contains(song)){
            throw new BadRequestException("User haven't liked this song!");
        }

        song.getLikes().remove(user);
        songRepository.delete(song);
        return song.getLikes().size();
    }

    public SongWithoutUserDTO getByTitle(String title) {
        Song song = utils.getSongByTitle(title);
        return modelMapper.map(song,SongWithoutUserDTO.class);
    }

    @SneakyThrows
    public String uploadSongPicture(long song_id,MultipartFile file, long id) {
        Song song = songRepository.getById(song_id);
        if(song.getOwner().getId() != id){
            throw new BadRequestException("Not allowed to modify songs of other users!");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("song_pictures" + File.separator + name);
        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        song.setCoverPhotoUrl(name);
        songRepository.save(song);
        return f.getName();
    }
}
