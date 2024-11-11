package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Director> directorRowMapper = (rs, rowNum) -> new Director(
            rs.getInt("director_id"),
            rs.getString("name")
    );

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "SELECT director_id, name FROM directors WHERE director_id = ?";
        return jdbcTemplate.query(sql, directorRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT director_id, name FROM directors";
        return jdbcTemplate.query(sql, directorRowMapper);
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, new String[] {"director_id"});
            ps.setString(1, director.getName());
            return ps;
        }, keyHolder);

        int generatedId = keyHolder.getKey().intValue();
        director.setId(generatedId);
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE director_id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sql = "DELETE FROM directors WHERE director_id = ?";
        jdbcTemplate.update(sql, id);
    }
}
