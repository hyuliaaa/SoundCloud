package com.example.soundcloud.model.POJO;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Entity(name = "songs")
@Getter
@Setter
@NoArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String title;

    @ManyToOne
    @JoinColumn(name="owner_id")
    private User owner;

    @Column
    private LocalDateTime uploadedAt;

    @Column
    private int views;

    @Column
    private String songUrl;

    @Column
    private String coverPhotoUrl;

    @Column
    private boolean isPublic;

    @ManyToMany
    @JoinTable(
            name = "users_like_songs",
            joinColumns = @JoinColumn(name = "song_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likes;

    //TODO add description
}
