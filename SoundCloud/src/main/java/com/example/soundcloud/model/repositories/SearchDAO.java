package com.example.soundcloud.model.repositories;

import com.example.soundcloud.exceptions.BadRequestException;
import com.example.soundcloud.model.DTO.song.SongSearchDTO;
import com.example.soundcloud.model.DTO.song.SongResponseDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Component
public class SearchDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final int RESULTS_PER_PAGE = 2;

    public List<SongResponseDTO> searchSongs(SongSearchDTO searchDTO, int pageNumber){
        if(searchDTO.getTitle() == null || searchDTO.getTitle().isBlank()){
            throw new BadRequestException("No results");
        }

        String title = searchDTO.getTitle();
        LocalDate after = searchDTO.getAfter();
        LocalDate before = searchDTO.getBefore();
        String tag = searchDTO.getTag();

        StringBuilder sql = new StringBuilder("SELECT s.* FROM songs AS s\n" +
                "JOIN descriptions AS d ON(s.description_id=d.id)\n" +
                "JOIN descriptions_have_tags AS dht ON(dht.description_id=d.id)\n" +
                "JOIN tags AS t ON(t.id=dht.tag_id)\n" +
                "LEFT JOIN users_like_songs AS uls ON(s.id = uls.song_id)\n" +
                "WHERE s.title LIKE \"%" + title + "%\"\n");

        if(before != null){
            sql.append("AND DATE(uploaded_at) < '" + Date.valueOf(before) + "'\n");
        }
        if(after != null){
            sql.append("AND DATE(uploaded_at) > '" + Date.valueOf(after) + "'\n");
        }
        if(tag != null && !tag.isBlank()){
            sql.append("AND t.name = \"" + tag + "\"\n");
        }
        sql.append("GROUP BY s.id\n" +
                "ORDER BY COUNT(uls.user_id) DESC, s.views DESC\n" +
                "LIMIT " + RESULTS_PER_PAGE + " OFFSET " + RESULTS_PER_PAGE * (pageNumber - 1));

        return jdbcTemplate.query(sql.toString(), resultSet -> {
            List<SongResponseDTO> songs = new LinkedList<>();

            while (resultSet.next()) {
                songs.add(songBuilder(resultSet));
            }
            return songs;
        });
    }

    private SongResponseDTO songBuilder(ResultSet resultSet) throws SQLException {
        return SongResponseDTO.builder().id(resultSet.getLong("id"))
                .title(resultSet.getString("title"))
                .uploadedAt(resultSet.getTimestamp("uploaded_at").toLocalDateTime())
                .views(resultSet.getInt("views"))
                .songUrl(resultSet.getString("song_url"))
                .coverPhotoUrl(resultSet.getString("cover_photo_url"))
                .isPublic(resultSet.getBoolean("is_public")).build();
    }
}