package com.example.soundcloud.controller;

import com.example.soundcloud.exceptions.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;

@RestController
public class FileController {

    @SneakyThrows
    @GetMapping("profile_pics/{filename}")
    public void downloadProfilePicture(@PathVariable String filename, HttpServletResponse response){
        File f = new File("profile_pictures" + File.separator + filename);
        if(!f.exists()){
            throw new NotFoundException("File doesn not exist!");
        }
        Files.copy(f.toPath(),response.getOutputStream());
    }
}
