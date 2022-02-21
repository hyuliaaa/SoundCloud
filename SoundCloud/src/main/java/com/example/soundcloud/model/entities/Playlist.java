package com.example.soundcloud.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "playlists")
@Setter
@Getter
@NoArgsConstructor
public class Playlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @Column
    private LocalDateTime createdAt;

    @Column
    private LocalDateTime lastModified;

    @Column
    private String coverPhotoUrl;

    @Column
    private boolean isPublic;



    @ManyToMany(mappedBy = "likedPlaylists")
    Set<User> likes;


    // TODO: 2/21/2022

    @OneToMany(mappedBy = "playlist")
    Set<Song> songs;

    //TODO: add description id



}
