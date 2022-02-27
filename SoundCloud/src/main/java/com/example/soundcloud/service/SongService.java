package com.example.soundcloud.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.exceptions.UnsupportedMediaTypeException;
import com.example.soundcloud.model.DTO.MessageDTO;
import com.example.soundcloud.model.DTO.song.*;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import com.example.soundcloud.model.entities.*;
import com.example.soundcloud.model.repositories.*;
import com.example.soundcloud.util.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
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
    private SearchDAO searchDAO;

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
    public SongResponseDTO upload(long id, SongUploadRequestDTO uploadDTO, MultipartFile file){

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

        return modelMapper.map(song, SongResponseDTO.class);
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

        Utils.validateSong(file);

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("songs" + File.separator + name);

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

        Upload upload = tm.upload(STORAGE_BUCKET_NAME, song.getSongUrl(), f);
    }

    public Set<SongResponseDTO> getAllUploaded (long id, long otherUserId){

        Set<SongResponseDTO> songs = utils.getUserById(otherUserId)
                .getUploadedSongs().stream()
                .map((song) -> modelMapper.map(song, SongResponseDTO.class))
                .collect(Collectors.toSet());

        if (id != otherUserId)
            songs = songs.stream().filter(SongResponseDTO::isPublic).collect(Collectors.toSet());

        return songs.stream().sorted(Comparator.comparing(SongResponseDTO::getUploadedAt))
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

    public Set<SongResponseDTO> getByTitle(String title) {
        Set <SongResponseDTO> songs = utils.getSongByTitle(title);
        return songs;
    }

    @SneakyThrows
    public MessageDTO uploadSongPicture(long song_id,MultipartFile file, long id) {
        Song song = utils.getSongById(song_id);
        if(song.getOwner().getId() != id){
            throw new BadRequestException("Not allowed to modify songs of other users!");
        }
        Utils.validateImage(file);
        if(song.getCoverPhotoUrl()!=null){
            File songPicture = new File("song_pictures" + File.separator + song.getCoverPhotoUrl());
            if (songPicture.exists()) {
                songPicture.delete();
            }
            else {
                throw new NotFoundException("No such song!");
            }
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("song_pictures" + File.separator + name);

        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        song.setCoverPhotoUrl(name);
        songRepository.save(song);
        return new MessageDTO(f.getName());
    }

    @Transactional
    public SongResponseDTO edit(long userId, SongEditRequestDTO requestDTO) {
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
        return modelMapper.map(song, SongResponseDTO.class);
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

    public byte[] downloadFromS3(long userId, long songId) {
        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if (!song.isPublic() && !song.getOwner().equals(user)){
            throw new NotFoundException("Song not found");
        }

        incrementViews(song.getSongUrl());
        songRepository.save(song);
        return downloadFile(song.getSongUrl());
    }

    @Async
    public byte[] downloadFile(final String keyName) {
        try {
            byte[] content;
            final S3Object s3Object = amazonS3.getObject(STORAGE_BUCKET_NAME, keyName);
            final S3ObjectInputStream stream = s3Object.getObjectContent();
            content = IOUtils.toByteArray(stream);
            s3Object.close();
            return content;
        } catch(AmazonS3Exception | IOException e) {
            throw new BadRequestException("Song could not be downloaded");
        }
    }


    public void incrementViews(String filename) {
        Song song = songRepository.findBySongUrl(filename).orElseThrow(() -> new NotFoundException("Song not found!"));
        song.setViews(song.getViews() + 1);
        songRepository.save(song);
    }

    @SneakyThrows
    public SongResponseDTO deleteSong(long songId, long userId) {
        User user = utils.getUserById(userId);
        Song song = utils.getSongById(songId);

        if(!song.getOwner().equals(user)){
            throw new BadRequestException("You cannot delete other user's song!");
        }
        songRepository.delete(song);
        File file = new File("songs" + File.separator + song.getSongUrl());
        if (file.exists()) {
             file.delete();
            if(song.getCoverPhotoUrl()!=null) {
                File songImage = new File("song_pictures" + File.separator + song.getCoverPhotoUrl());
                if (songImage.exists()) {
                    songImage.delete();
                }
            }
        }
        else {
            throw new NotFoundException("No such song!");
        }

        if (amazonS3.doesObjectExist(STORAGE_BUCKET_NAME, song.getSongUrl())){
            amazonS3.deleteObject(STORAGE_BUCKET_NAME, song.getSongUrl());
        }

        return modelMapper.map(song,SongResponseDTO.class);

    }

    public List<SongResponseDTO> searchSongs(SongSearchDTO searchDTO, int pageNumber) {
        return searchDAO.searchSongs(searchDTO, pageNumber);
    }
}
