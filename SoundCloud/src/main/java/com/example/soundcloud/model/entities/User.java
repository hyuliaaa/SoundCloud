package com.example.soundcloud.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true)
    private String username;

    @Column(unique = true)
    private String email;

    @Column
    private String password;

    @Column
    private int age;

    @Column
    private char gender;

    @Column
    private LocalDateTime createdAt;

    @Column
    private String profilePictureUrl;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL)
    private Set<Song> uploadedSongs;

    @OneToMany(mappedBy = "owner")
    private Set<Playlist> playlists;

    @ManyToMany (mappedBy = "likes")
    private Set<Song> likedSongs;


    @ManyToMany
    @JoinTable(
            name = "users_follow_users",
            joinColumns = @JoinColumn(name = "follower_id"),
            inverseJoinColumns = @JoinColumn(name = "followed_id")
    )
    private Set<User> following;

    @ManyToMany (mappedBy = "following")
    private Set<User> followers;

    @ManyToMany()
    @JoinTable(
            name = "users_like_playlists",
            joinColumns = @JoinColumn(name="user_id"),
            inverseJoinColumns = @JoinColumn(name="playlist_id")
    )
    private Set<Playlist> likedPlaylists;


}
