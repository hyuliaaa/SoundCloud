package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.exceptions.UnsupportedMediaTypeException;
import com.example.soundcloud.model.DTO.song.SongEditRequestDTO;
import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithLikesDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.entities.*;
import com.example.soundcloud.model.repositories.DescriptionRepository;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.TagRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import javax.transaction.Transactional;
import java.io.File;
import java.net.URLConnection;
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
    public SongWithoutUserDTO upload(long id, SongUploadRequestDTO uploadDTO, MultipartFile file){

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(utils.getUserById(id));
        song.setUploadedAt(LocalDateTime.now());
        song.setSongUrl(uploadSongFile(file));

        Description description = song.getDescription();
        Set<Tag> tags = new HashSet<>();

        if (description != null){
            tags = getTags(description.getContent());

            Set<Tag> filtered = tags.stream().filter(tag -> tagRepository.findTagByName(tag.getName()).isEmpty())
                    .collect(Collectors.toSet());
            description.getTags().addAll(filtered);
        }
        songRepository.save(song);

        if (description != null){
            tags.stream().filter(tag -> tagRepository.findTagByName(tag.getName()).isPresent())
                    .map(tag -> tagRepository.findTagByName(tag.getName()).get())
                    .forEach(tag -> description.getTags().add(tag));
            descriptionRepository.save(description);
        }

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

    @SneakyThrows
    private String uploadSongFile(MultipartFile file){

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("songs" + File.separator + name);

        String mimeType = Files.probeContentType(f.toPath());
        if (!mimeType.contains("audio" + File.separator)){
            throw new UnsupportedMediaTypeException("You must provide an audio file");
        }

        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        return f.getName();
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

    public SongWithLikesDTO like(long songId, long userId) {

        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if (!song.isPublic() && song.getOwner() != user)
            throw new BadRequestException("Song is private");

        if(user.getLikedSongs().contains(song)){
            throw new BadRequestException("User already liked this song!");
        }

        SongWithLikesDTO dto = new SongWithLikesDTO();
        song.getLikes().add(user);
        songRepository.save(song);
        modelMapper.map(song,dto);
        dto.setNumberOfLikes(song.getLikes().size());
        return dto;
    }


    public SongWithLikesDTO unlike(long songId, long userId) {

        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if (!song.isPublic() && song.getOwner() != user)
            throw new BadRequestException("Song is private");

        if(!user.getLikedSongs().contains(song)){
            throw new BadRequestException("User haven't liked this song!");
        }

        SongWithLikesDTO dto = new SongWithLikesDTO();
        song.getLikes().remove(user);
        modelMapper.map(song,dto);
        dto.setNumberOfLikes(song.getLikes().size());
        songRepository.save(song);
        return dto;
    }

    public SongWithoutUserDTO getByTitle(String title) {
        Song song = utils.getSongByTitle(title);
        return modelMapper.map(song,SongWithoutUserDTO.class);
    }

    @SneakyThrows
    public String uploadSongPicture(long song_id,MultipartFile file, long id) {
        Song song = utils.getSongById(song_id);
        if(song.getOwner().getId() != id){
            throw new BadRequestException("Not allowed to modify songs of other users!");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("song_pictures" + File.separator + name);

        String mimeType = Files.probeContentType(f.toPath());
        if (!mimeType.contains("image" + File.separator)){
            throw new UnsupportedMediaTypeException("You must provide an image file");
        }

        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        song.setCoverPhotoUrl(name);
        songRepository.save(song);
        return f.getName();
    }

    public SongWithoutUserDTO edit(long userId, SongEditRequestDTO requestDTO) {
        Song song = utils.getSongById(requestDTO.getId());
        if (song.getOwner().getId() != userId){
            throw new ForbiddenException("You cannot edit this song!");
        }

        song.setTitle(requestDTO.getTitle());
        song.setPublic(requestDTO.getIsPublic());

        if(requestDTO.getDescription() == null){
            Description description = song.getDescription();
            song.setDescription(null);
            descriptionRepository.delete(description);
        }
        else{
            if (song.getDescription() != null) {
                if (!requestDTO.getDescription().getContent().equals(song.getDescription().getContent())) {
                    Description description = song.getDescription();
                    song.setDescription(modelMapper.map(requestDTO.getDescription(), Description.class));
                    descriptionRepository.delete(description);
                }
            }
            else {
                song.setDescription(modelMapper.map(requestDTO.getDescription(), Description.class));
            }
        }

        songRepository.save(song);
        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    @SneakyThrows
    public SongUploadRequestDTO toJson(String stringDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(stringDto, SongUploadRequestDTO.class);
    }

//    public void delete(long userId, long songId) {
//        User user = utils.getUserById(userId);
//        Song song = utils.getSongById(songId);
//
//        if (!song.getOwner().equals(user)){
//            throw new ForbiddenException("You cannot delete this song!");
//        }
//
//        songRepository.delete(song);
//    }
}
