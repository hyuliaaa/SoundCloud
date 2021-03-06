package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.ForbiddenException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.DTO.MessageDTO;
import com.example.soundcloud.model.DTO.playlist.*;
import com.example.soundcloud.model.entities.Playlist;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.PlaylistRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import com.example.soundcloud.util.Utils;
import lombok.Data;
import lombok.SneakyThrows;
import org.apache.commons.io.FilenameUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;


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

    public PlaylistResponseDTO createPlaylist(long id, PlaylistCreateRequestDTO createPlaylistDTO) {
        PlaylistResponseDTO dto = new PlaylistResponseDTO();
        Playlist playlist = modelMapper.map(createPlaylistDTO,Playlist.class);
      //  playlist.setPublic(createPlaylistDTO.isPublic());
        playlist.setCreatedAt(LocalDateTime.now());
        playlist.setLastModified(LocalDateTime.now());
        playlist.setOwner(utils.getUserById(id));
        playlistRepository.save(playlist);
        modelMapper.map(playlist,dto);
        dto.setOwnerId(id);
        return dto;
    }

    @SneakyThrows
    public MessageDTO uploadPlaylistPicture(long playlistId, MultipartFile file, long ownerId) {
        Playlist playlist = utils.getPlaylistById(playlistId);
        if(playlist.getOwner().getId() != ownerId){
            throw new BadRequestException("Not allowed to modify playlist pictures of other users!");
        }
        Utils.validateImage(file);
        if(playlist.getCoverPhotoUrl()!=null){
            File playlistPicture = new File("playlist_pictures" + File.separator + playlist.getCoverPhotoUrl());
            if (playlistPicture.exists()) {
                playlistPicture.delete();
            }
            else {
                throw new NotFoundException("No playlist picture!");
            }
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("playlist_pictures" + File.separator + name);
        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        playlist.setCoverPhotoUrl(name);
        playlist.setLastModified(LocalDateTime.now());
        playlistRepository.save(playlist);
        return new MessageDTO(f.getName());
    }

    public PlaylistWithLikesDTO like(long playlistId, long userId) {

        User user = utils.getUserById(userId);
        Playlist playlist = utils.getPlaylistById(playlistId);

        if (!playlist.isPublic() && playlist.getOwner().getId() != userId)
            throw new BadRequestException("Playlist is private");

        if(user.getLikedPlaylists().contains(playlist)){
            throw new BadRequestException("User already liked this playlist!");
        }

        PlaylistWithLikesDTO dto = new PlaylistWithLikesDTO();
        playlist.getLikes().add(user);
        playlistRepository.save(playlist);
        modelMapper.map(playlist,dto);
        dto.setNumberOfLikes(playlist.getLikes().size());
        return dto;
    }

    public PlaylistWithLikesDTO unlike(long playlistId, long userId) {
        User user = utils.getUserById(userId);
        Playlist playlist = utils.getPlaylistById(playlistId);

        if (!playlist.isPublic() && playlist.getOwner() != user)
            throw new BadRequestException("Playlist is private!");

        if(!user.getLikedPlaylists().contains(playlist)){
            throw new BadRequestException("User haven't liked this playlist!");
        }

        PlaylistWithLikesDTO dto = new PlaylistWithLikesDTO();
        playlist.getLikes().remove(user);
        modelMapper.map(playlist,dto);
        dto.setNumberOfLikes(playlist.getLikes().size());
        playlistRepository.save(playlist);
        return dto;
    }

    public PlaylistWithSongsDTO addSong(long playlistId,long songId, long userId) {
        User user = utils.getUserById(userId);
        Playlist playlist = utils.getPlaylistById(playlistId);
        Song song = utils.getSongById(songId);
        if(playlist.getSongs().contains(song)){
            throw new BadRequestException("This song is already in the playlist!");
        }
        playlist.getSongs().add(song);
        playlist.setLastModified(LocalDateTime.now());
        PlaylistWithSongsDTO dto = new PlaylistWithSongsDTO();
        playlistRepository.save(playlist);
        modelMapper.map(playlist,dto);
        dto.setNumberOfSongs(playlist.getSongs().size());
        return dto;
    }

    public PlaylistWithSongsDTO deleteSong(long playlistId, long songId, long userId) {
        User user = utils.getUserById(userId);
        Playlist playlist = utils.getPlaylistById(playlistId);
        Song song = utils.getSongById(songId);
        if(!playlist.getSongs().contains(song)){
            throw new BadRequestException("This song is not in the list!");
        }
        playlist.getSongs().remove(song);
        playlist.setLastModified(LocalDateTime.now());
        playlistRepository.save(playlist);
        PlaylistWithSongsDTO dto = new PlaylistWithSongsDTO();
        modelMapper.map(playlist,dto);
        dto.setNumberOfSongs(playlist.getSongs().size());
        return dto;

    }

    public PlaylistResponseDTO getByid(long playlistId, long userId) {
        Playlist playlist = utils.getPlaylistById(playlistId);
        User user = utils.getUserById(userId);
        PlaylistResponseDTO dto = new PlaylistResponseDTO();
        modelMapper.map(playlist,dto);
        return dto;
    }

    public Set<PlaylistResponseDTO> getByTitle(String title) {
        Set<PlaylistResponseDTO> playlists = utils.getPlaylistByTitle(title);
        return playlists;
    }

    public void delete(long playlistId, long userId) {
        User user = utils.getUserById(userId);
        Playlist playlist =utils.getPlaylistById(playlistId);
        if(!playlist.getOwner().equals(user)){
            throw new ForbiddenException("You cannot delete other user's playlists!");
        }
        if(playlist.getCoverPhotoUrl()!=null){
            File playlistPicture = new File("playlist_pictures" + File.separator + playlist.getCoverPhotoUrl());
            if (playlistPicture.exists()) {
                playlistPicture.delete();
            }
            else {
                throw new NotFoundException("No playlist picture!");
            }
        }
        playlistRepository.delete(playlist);
    }

    public PlaylistResponseDTO edit(long userId, PlaylistEditDTO dto) {
        Playlist playlist = utils.getPlaylistById(dto.getId());
        if(playlist.getOwner().getId()!=userId){
            throw new ForbiddenException("You cannot edit this playlist!");
        }

        playlist.setTitle(dto.getTitle());
        playlist.setPublic(dto.getIsPublic());
        playlist.setLastModified(LocalDateTime.now());
        playlistRepository.save(playlist);
        return modelMapper.map(playlist,PlaylistResponseDTO.class);
    }

    public Set<PlaylistResponseDTO> getAllUserLikedPlaylists(long userId) {
        Set<PlaylistResponseDTO> playlistResponseDTOS = utils.getUserById(userId)
                .getLikedPlaylists().stream()
                .map(playlist -> modelMapper.map(playlist,PlaylistResponseDTO.class)).collect(Collectors.toSet());
        return playlistResponseDTOS;
    }

    public PlaylistWithSongsInfo getAllSongs(long playlistId, long userId) {
        Playlist playlist = utils.getPlaylistById(playlistId);
        System.out.println("=========" + playlist.getId() +" "+playlist.getTitle());
        System.out.println(playlist.isPublic());
        User user = utils.getUserById(userId);
        if(!(playlist.isPublic())){
            if(playlist.getOwner().getId()!=userId) {
                throw new NotFoundException("Playlist not found!");
            }
        }

        return modelMapper.map(playlist,PlaylistWithSongsInfo.class);
    }
}
