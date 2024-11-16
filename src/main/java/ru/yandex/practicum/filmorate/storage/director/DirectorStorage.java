package ru.yandex.practicum.filmorate.storage.director;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.storage.base.BaseStorage;

import java.util.*;

@Repository
public class DirectorStorage extends BaseStorage<Director> implements DirectorRepository {

    public DirectorStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Director> mapper) {
        super(jdbc, mapper);
    }

    private static final String SQL_GET_ALL_DIRECTORS =
            "SELECT * FROM directors;";

    private static final String SQL_GET_DIRECTOR_BY_ID =
            "SELECT * FROM directors WHERE director_id=:director_id;";

    private static final String SQL_INSERT_DIRECTOR =
            "INSERT INTO directors (director_name) " +
                    "VALUES (:director_name);";

    private static final String SQL_UPDATE_DIRECTOR =
            "UPDATE directors SET director_name=:director_name WHERE director_id=:director_id;";

    private static final String SQL_DELETE_DIRECTOR =
            "DELETE FROM directors WHERE director_id=:director_id";

    @Override
    public List<Director> getAll() {
        return getMany(SQL_GET_ALL_DIRECTORS);
    }

    @Override
    public Optional<Director> getDirectorById(Integer id) {
        Map<String, Object> params = Map.of("director_id", id);
        Optional<Director> director = getOne(SQL_GET_DIRECTOR_BY_ID, params);
        return director;
    }

    @Override
    public Director addDirector(Director director) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_name", director.getName());
        jdbc.update(SQL_INSERT_DIRECTOR, params, keyHolder);
        director.setId(keyHolder.getKeyAs(Integer.class));
        return director;
    }

    @Override
    public void deleteDirector(Integer id) {
        Map<String, Object> params = Map.of("director_id", id);
        jdbc.update(SQL_DELETE_DIRECTOR, params);
    }

    @Override
    public Director updateDirector(Director director) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("director_id", director.getId());
        params.addValue("director_name", director.getName());
        jdbc.update(SQL_UPDATE_DIRECTOR, params);
        return getDirectorById(director.getId()).get();
    }
}