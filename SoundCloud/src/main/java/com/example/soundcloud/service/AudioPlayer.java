package com.example.soundcloud.service;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.exceptions.NotFoundException;
import com.example.soundcloud.model.entities.Song;
import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class AudioPlayer{

    private AdvancedPlayer advancedPlayer;
    private Song song;

    public void play(Song song){

        File file = new File("songs/" + song.getSongUrl());

        try (FileInputStream inputStream = new FileInputStream(file)) {
            this.song = song;
            this.advancedPlayer = new AdvancedPlayer(inputStream);
            this.advancedPlayer.play();
        } catch (FileNotFoundException e) {
            throw new NotFoundException("Song was not found in file system");
        } catch (Exception e) {
            throw new RuntimeException("Song could not be played");
        }
    }

    public void stop(){
        if (advancedPlayer == null){
            throw new BadRequestException("No audio is currently playing");
        }
        advancedPlayer.close();
    }

    public Song getSong() {
        return song;
    }
}
