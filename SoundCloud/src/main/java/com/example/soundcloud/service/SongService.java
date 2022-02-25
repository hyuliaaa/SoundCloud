package com.example.soundcloud.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.exceptions.NotFoundException;
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
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Data
public class SongService {

    private static final String STORAGE_BUCKET_NAME = "amazon-soundcloud";

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

    @Autowired
    private AmazonS3 amazonS3;

    //userId, player
    private ConcurrentHashMap<Long, AudioPlayer> userPlayers = new ConcurrentHashMap<>();


    @Transactional
    public SongWithoutUserDTO upload(long id, SongUploadRequestDTO uploadDTO, MultipartFile file){

        Song song = modelMapper.map(uploadDTO, Song.class);
        song.setOwner(utils.getUserById(id));
        song.setUploadedAt(LocalDateTime.now());
        song.setSongUrl(uploadSongFile(file));

        Description description = song.getDescription();

        if (description != null){
            createTagsForDescription(description);
        }
        songRepository.save(song);
        uploadToAWS(song);

        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    private void createTagsForDescription(Description description){
        Set<Tag> tags = getTags(description.getContent());
        List<Tag> savedTags = tagRepository.findAll();
        Set<Tag> unsaved = tags.stream().filter(tag -> !savedTags.contains(tag)).collect(Collectors.toSet());
        tagRepository.saveAll(unsaved);
        description.getTags().addAll(tagRepository.findAllByNameIn(tags.stream().map(Tag::getName).collect(Collectors.toSet())));
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

        //todo
        String mimeType = Files.probeContentType(f.toPath());
        if (!mimeType.contains("audio" + File.separator)){
            throw new UnsupportedMediaTypeException("You must provide an audio file");
        }

        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        return f.getName();
    }

    private void uploadToAWS(Song song){
        File f = new File("songs" + File.separator + song.getSongUrl());
        if(!f.exists()){
            throw new NotFoundException("File does not not exist!");
        }

        TransferManager tm = TransferManagerBuilder.standard()
                .withS3Client(amazonS3)
                .withMultipartUploadThreshold((long) (5 * 1024 * 1025))
                .build();

        //todo songs must have unique names
        Upload upload = tm.upload(STORAGE_BUCKET_NAME, song.getTitle(), f);
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

    public Page<SongWithLikesDTO> getAllPublicSongs(int offset, int pageSize, String field) {
        Page <SongWithLikesDTO> songs = utils.getAllSongs(offset,pageSize,field);
        return songs;
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

    public Set<SongWithoutUserDTO> getByTitle(String title) {
        Set <SongWithoutUserDTO> songs = utils.getSongByTitle(title);
        return songs;
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

    @Transactional
    public SongWithoutUserDTO edit(long userId, SongEditRequestDTO requestDTO) {
        Song song = utils.getSongById(requestDTO.getId());
        if (song.getOwner().getId() != userId){
            throw new ForbiddenException("You cannot edit this song!");
        }

        song.setTitle(requestDTO.getTitle());
        song.setPublic(requestDTO.getIsPublic());

        Description description = song.getDescription();

        if (description != null) {
            song.setDescription(null);
            description.setTags(null);
            descriptionRepository.delete(description);
        }

        if(requestDTO.getDescription() != null){
            Description newDescription = modelMapper.map(requestDTO.getDescription(), Description.class);
            createTagsForDescription(newDescription);
            song.setDescription(newDescription);
        }
        songRepository.save(song);
        return modelMapper.map(song, SongWithoutUserDTO.class);
    }

    @SneakyThrows
    public SongUploadRequestDTO toJson(String stringDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(stringDto, SongUploadRequestDTO.class);
    }
//
//    public List <SongWithLikesDTO> orderByLikesAsc() {
//        List <SongWithLikesDTO> dto = utils.findByOrderByLikesAsc();
//        return dto;
//    }

    public void playAudio(long userId, long songId) {
        User user = userRepository.getById(userId);
        Song song = songRepository.getById(songId);

        if (!song.isPublic() && !song.getOwner().equals(user)) {
            throw new BadRequestException("Song not found");
        }
        if (userPlayers.containsKey(userId)){
            throw new BadRequestException("You are already playing a song");
        }

        AudioPlayer player = new AudioPlayer();
        userPlayers.put(userId, player);
        player.play(song);
    }

    public void stopAudio(long userId) {

        AudioPlayer player = userPlayers.get(userId);
        if (player == null){
            throw new BadRequestException("No audio is currently playing");
        }

        Song song = player.getSong();
        player.stop();
        userPlayers.remove(userId);

        song.setViews(song.getViews() + 1);
        songRepository.save(song);
    }


//    public void delete(long songId, long userId) {
//        User user = utils.getUserById(userId);
//        Song song = utils.getSongById(songId);
//
//        if (!(song.getOwner().equals(user))){
//            throw new ForbiddenException("You cannot delete this song!");
//        }
//        songRepository.delete(song);
//    }

}
