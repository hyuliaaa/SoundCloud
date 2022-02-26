package com.example.soundcloud.controller;

import com.example.soundcloud.model.DTO.song.SongEditRequestDTO;
import com.example.soundcloud.model.DTO.song.SongUploadRequestDTO;
import com.example.soundcloud.model.DTO.song.SongWithLikesDTO;
import com.example.soundcloud.model.DTO.song.SongWithoutUserDTO;
import com.example.soundcloud.model.DTO.user.UserResponseDTO;
import com.example.soundcloud.service.SongService;
import javazoom.jl.decoder.JavaLayerException;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import java.io.FileNotFoundException;
import java.util.List;
import java.util.Set;

import static com.example.soundcloud.controller.UserController.USER_ID;

@RestController
@Data
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping(value = "/upload_song", consumes = { MediaType.APPLICATION_JSON_VALUE,MediaType.MULTIPART_FORM_DATA_VALUE })
    ResponseEntity<SongWithoutUserDTO> upload(@Valid @RequestParam("dto") String stringDto, @RequestParam("file") MultipartFile file, HttpSession session){
        long id = (long) session.getAttribute(USER_ID);
        SongUploadRequestDTO dto = songService.toJson(stringDto);
        return new ResponseEntity<>(songService.upload(id, dto, file), HttpStatus.CREATED);
    }

    @GetMapping(value = "/songs/download/{id}", produces = "audio/mpeg")
    ResponseEntity<byte[]> downloadFromS3(@PathVariable("id") long songId, HttpSession session){
        long userId = (long) session.getAttribute(USER_ID);
        return ResponseEntity.ok(songService.downloadFromS3(userId, songId));
    }

    @GetMapping("/users/{id}/songs")
    ResponseEntity<Set<SongWithoutUserDTO>> getAllUploadedByUserId(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.getAllUploaded((long) session.getAttribute(USER_ID), id));
    }

    @GetMapping("/paginationAndSort/{offset}/{pageSize}/{field}")
    ResponseEntity<Page<SongWithLikesDTO>> getAllPublicSongs(@PathVariable int offset, @PathVariable int pageSize, @PathVariable String field){
        return ResponseEntity.ok(songService.getAllPublicSongs(offset,pageSize,field));

    }

    @PostMapping("/songs/{id}/like")
    ResponseEntity<SongWithLikesDTO>like(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.like(id,(long)session.getAttribute(USER_ID)));
    }

    @DeleteMapping("/songs/{id}/unlike")
    ResponseEntity<SongWithLikesDTO>unlike(@PathVariable long id, HttpSession session){
        return ResponseEntity.ok(songService.unlike(id, (long) session.getAttribute(USER_ID)));
    }

    @GetMapping("/songs/{title}")
    public ResponseEntity<Set<SongWithoutUserDTO>> getSongByTitle(@PathVariable String title){
        return ResponseEntity.ok(songService.getByTitle(title));
    }

    @PostMapping("/songs/{song_id}/upload-song-image")
    public String uploadSongImage(@PathVariable("song_id") long songId, @RequestParam(name = "picture") MultipartFile file, HttpSession session){
        return songService.uploadSongPicture(songId,file, (long) session.getAttribute(USER_ID));
    }

    @PutMapping("/songs/edit")
    public SongWithoutUserDTO edit(@Valid @RequestBody SongEditRequestDTO requestDTO, HttpSession session){
        return songService.edit((long) session.getAttribute(USER_ID), requestDTO);
    }

    @GetMapping("/songs/{id}/play")
    public ResponseEntity<String> playAudio(@PathVariable long id, HttpSession session) {

        songService.playAudio((long) session.getAttribute(USER_ID), id);
        return ResponseEntity.ok("Song is playing");
    }

    @GetMapping("/songs/stop")
    public ResponseEntity<String> stopAudio(HttpSession session) {

        songService.stopAudio((long) session.getAttribute(USER_ID));
        return ResponseEntity.ok("Song was stopped");
    }


//    @GetMapping("songs/order-songs-by/{order}")
//    public ResponseEntity<List<SongWithLikesDTO>> orderSongsByLikes(@PathVariable String order){
////        if(order.equals("ASC")){
//            return ResponseEntity.ok(songService.orderByLikesAsc());
//////        }
////        return ResponseEntity.ok(songService.orderByLikesDesc());
//    }



//    @DeleteMapping("/songs/{id}")
//    ResponseEntity<String> delete(@PathVariable(name = "id") long songId, HttpSession session){
//        songService.delete((long) session.getAttribute(USER_ID), songId);
//        return ResponseEntity.ok("Songs was deleted");
//    }
}
