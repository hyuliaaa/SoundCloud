package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.playlist.PlaylistCreateRequestDTO;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.DTO.playlist.PlaylistWithLikesDTO;
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

    @SneakyThrows
    public String uploadPlaylistPicture(long playlistId,MultipartFile file, long ownerId) {
        Playlist playlist = playlistRepository.getById(playlistId);
        if(playlist.getOwner().getId() != ownerId){
            throw new BadRequestException("Not allowed to modify playlist pictures of other users!");
        }
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        String name = System.nanoTime() + "." + extension;
        File f = new File("playlist_pictures" + File.separator + name);
        Files.copy(file.getInputStream(), Path.of(f.toURI()));
        playlist.setCoverPhotoUrl(name);
        playlistRepository.save(playlist);
        return f.getName();
    }

    public PlaylistWithLikesDTO like(long playlistId, long userId) {

        User user = utils.getUserById(userId);
        Playlist playlist = utils.getPlaylistById(playlistId);

        if (!playlist.isPublic() && playlist.getOwner() != user)
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

}
