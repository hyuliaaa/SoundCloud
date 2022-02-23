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


    @ManyToMany
    @JoinTable(
            name = "users_like_playlists",
            joinColumns = @JoinColumn(name="playlist_id"),
            inverseJoinColumns = @JoinColumn(name="user_id")
    )
    Set<User> likes;

    @ManyToMany
    @JoinTable(
            name = "playlists_have_songs",
            joinColumns = @JoinColumn(name="playlist_id"),
            inverseJoinColumns = @JoinColumn(name = "song_id")
    )
    Set<Song> songs;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "description_id", referencedColumnName = "id")
    private Description description;


}
