package ru.yandex.practicum.filmorate.storage.director;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcDirectorStorage implements DirectorStorage {
    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Director> directorRowMapper = (rs, rowNum) -> new Director(
            rs.getInt("id"),
            rs.getString("name")
    );

    @Override
    public Optional<Director> getDirectorById(int id) {
        String sql = "SELECT * FROM directors WHERE id = ?";
        return jdbcTemplate.query(sql, directorRowMapper, id).stream().findFirst();
    }

    @Override
    public List<Director> getAllDirectors() {
        String sql = "SELECT director_id, name FROM directors";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            int id = rs.getInt("director_id");
            String name = rs.getString("name");
            return new Director(id, name);
        });
    }

    @Override
    public Director createDirector(Director director) {
        String sql = "INSERT INTO directors (name) VALUES (?)";
        jdbcTemplate.update(sql, director.getName());
        return director;
    }

    @Override
    public Director updateDirector(Director director) {
        String sql = "UPDATE directors SET name = ? WHERE id = ?";
        jdbcTemplate.update(sql, director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirector(int id) {
        String sql = "DELETE FROM directors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}