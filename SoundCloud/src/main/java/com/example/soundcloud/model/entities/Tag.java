package com.example.soundcloud.model.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag {

    public Tag(String name){
        setName(name);
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    private String name;
}
