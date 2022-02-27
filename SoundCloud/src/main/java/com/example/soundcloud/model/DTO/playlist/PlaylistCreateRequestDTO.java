package com.example.soundcloud.model.DTO.playlist;

import com.example.soundcloud.model.entities.User;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.time.LocalDateTime;

@Data
public class PlaylistCreateRequestDTO {

    @Column
    private String title;

    @Column
    private boolean isPublic;

    @JsonProperty("isPublic")
    public boolean isPublic() {
        return isPublic;
    }
}
