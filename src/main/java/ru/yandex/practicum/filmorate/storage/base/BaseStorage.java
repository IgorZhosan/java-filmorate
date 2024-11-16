package ru.yandex.practicum.filmorate.storage.base;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
public class BaseStorage<T> {

    protected final NamedParameterJdbcTemplate jdbc;
    protected final RowMapper<T> mapper;

    protected Optional<T> getOne(String query, Map<String, Object> params) {
        try {
            T res = jdbc.queryForObject(query, params, mapper);
            return Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException ignored) {
            return Optional.empty();
        }
    }

    protected List<T> getMany(String query) {
        return jdbc.query(query, mapper);
    }

    protected List<T> getMany(String query, Map<String, Object> params) {
        return jdbc.query(query, params, mapper);
    }
}