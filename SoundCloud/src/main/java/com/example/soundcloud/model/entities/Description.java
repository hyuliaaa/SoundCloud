package com.example.soundcloud.model.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "descriptions")
@Getter
@Setter
@NoArgsConstructor
public class Description {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String content;

    @ManyToMany
    @JoinTable(
            name = "descriptions_have_tags",
            joinColumns = @JoinColumn(name="description_id"),
            inverseJoinColumns = @JoinColumn(name="tag_id")
    )
    private Set<Tag> tags;
}
