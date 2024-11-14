package ru.yandex.practicum.filmorate.storage.mpa;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JdbcMpaStorage implements MpaStorage {
    private final NamedParameterJdbcOperations jdbc;

    @Override
    public Optional<Mpa> getMpaById(final int id) {
        String sql = "SELECT mpa_id, mpa_name FROM mpa WHERE mpa_id = :mpa_id";
        Map<String, Object> params = Map.of("mpa_id", id);

        return Optional.ofNullable(jdbc.query(sql, params, rs -> {
            if (!rs.next()) {
                return null;
            }
            return new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name"));
        }));
    }

    @Override
    public List<Mpa> getAllMpa() {
        String sql = "SELECT * FROM mpa;";
        return jdbc.query(sql, (rs, rowNum)
                -> new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
    }
}