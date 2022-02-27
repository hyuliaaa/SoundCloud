package com.example.soundcloud.util;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.exceptions.UnsupportedMediaTypeException;
import com.example.soundcloud.model.DTO.playlist.PlaylistResponseDTO;
import com.example.soundcloud.model.DTO.song.SongResponseDTO;
import com.example.soundcloud.model.DTO.song.SongWithLikesDTO;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import com.example.soundcloud.model.entities.Comment;
import com.example.soundcloud.model.entities.Playlist;
import com.example.soundcloud.model.entities.Song;
import com.example.soundcloud.model.entities.User;
import com.example.soundcloud.model.repositories.CommentRepository;
import com.example.soundcloud.model.repositories.PlaylistRepository;
import com.example.soundcloud.model.repositories.SongRepository;
import com.example.soundcloud.model.repositories.UserRepository;
import lombok.SneakyThrows;
import org.apache.tika.Tika;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Component
public class Utils {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private ModelMapper modelMapper;

    public User getUserById(long id){
        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found!"));
    }

    public Page<UserResponseDTO> getUserByUsername(int offset, int pageSize, String username){
        Pageable pageable = PageRequest.of(offset,pageSize);
        Page <UserResponseDTO> users =  userRepository.findByUsernameStartsWith(username,pageable)
                .map(user -> modelMapper.map(user,UserResponseDTO.class));
        if(users.getSize()==0){
            throw new NotFoundException("No available users with that name!");
        }

        return users;

    }


    public User getUserByEmail(String email){
        return userRepository.findByEmail(email).orElseThrow(()->new NotFoundException("Email not found!"));
    }

    public Song getSongById(long id){
        return songRepository.findById(id).orElseThrow(() -> new NotFoundException("Song not found!"));
    }

    public Comment getCommentById(long id){
        return commentRepository.findById(id).orElseThrow(() -> new NotFoundException("Comment not found!"));
    }

    public Playlist getPlaylistById(long id){
       return playlistRepository.findById(id).orElseThrow(() -> new NotFoundException("Playlist not found!"));
    }

    public Set<SongResponseDTO> getSongByTitle(String title){

        Set <SongResponseDTO> songs = songRepository.findByTitleStartsWith(title)
                .stream().map(song ->modelMapper.map(song, SongResponseDTO.class))
                .collect(Collectors.toSet());
        if(songs.size()==0){
            throw new NotFoundException("No available songs with that name");
        }
        return songs;
    }

    public Set<PlaylistResponseDTO> getPlaylistByTitle(String title)
    {
        Set <PlaylistResponseDTO> playlists =  playlistRepository.findByTitleStartsWith(title)
                .stream().map(playlist ->modelMapper.map(playlist,PlaylistResponseDTO.class))
                .collect(Collectors.toSet());
        if(playlists.size()==0){
            throw new NotFoundException("No available playlists with that title!");
        }
        return playlists;
    }

    public List<SongWithLikesDTO> findByOrderByLikesAsc() {
        List<Song> songs = songRepository.findByOrderByLikesAsc();
        if(songs.size()==0){
            throw new NotFoundException("No available songs!");
        }
        List <SongWithLikesDTO> songsDto = new ArrayList<>();
        SongWithLikesDTO dto = new SongWithLikesDTO();
        for (Song song : songs) {
            modelMapper.map(song,dto);
            dto.setNumberOfLikes(song.getLikes().size());
            songsDto.add(dto);

        }
        return songsDto;
    }

    public Page<SongWithLikesDTO> getAllSongs(int offset, int pageSize,String field){
        if(field.isBlank()){
            field ="id";
        }
        Page<Song> songs = songRepository.findAll(PageRequest.of(offset, pageSize).withSort(Sort.by(field)));
        if(songs.getSize()==0){
            throw new NotFoundException("No available songs!");
        }

        List<SongWithLikesDTO> songsWithLikes = new ArrayList<>();
        for (Song song : songs) {
            SongWithLikesDTO dto = new SongWithLikesDTO();
            modelMapper.map(song,dto);
            dto.setNumberOfLikes(song.getLikes().size());
            songsWithLikes.add(dto);
        }

        return new PageImpl<>(songsWithLikes.stream()
                                            .filter(songWithLikesDTO -> songWithLikesDTO.isPublic())
                                            .collect(Collectors.toList()));
    }

    public Page<SongResponseDTO> getLikedSongs(int offset, int pageSize, long id) {
        User user = getUserById(id);
        Set<Song> songs = user.getLikedSongs();
        if(user.getLikedSongs().size() == 0){
            throw new BadRequestException("This user does not have liked songs!");
        }

        Pageable pageable = PageRequest.of(offset,pageSize);
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), songs.size());

        List <SongResponseDTO> list = songs.stream().
                map(song -> modelMapper.map(song, SongResponseDTO.class))
                .collect(Collectors.toList());
        return new PageImpl<SongResponseDTO>(list.subList(start,end),pageable, list.size());
    }

    @SneakyThrows
    public static void validateImage(MultipartFile file){
        Tika tika = new Tika();
        String type = null;
        type = tika.detect(file.getInputStream());
        if(!type.contains("image")){
            throw new UnsupportedMediaTypeException("You must provide an image file!");
        }
    }

    @SneakyThrows
    public static void validateSong(MultipartFile file){
        Tika tika = new Tika();
        String type = tika.detect(file.getInputStream());
        if(!type.contains("audio")){
            throw new UnsupportedMediaTypeException("You must provide an audio file!");
        }
    }


}