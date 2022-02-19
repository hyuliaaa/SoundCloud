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
    private LocalDateTime createAt;

    @Column
    private LocalDateTime lastModified;

    @Column
    private String coverPhotUrl;

    @Column
    private boolean isPublic;



    @ManyToMany(mappedBy = "likedPlaylists")
    Set<User> likes;

    //TODO: add description id



}
