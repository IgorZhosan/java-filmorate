package ru.yandex.practicum.filmorate.storage.film.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class FilmsExtractor implements ResultSetExtractor<Map<Integer, Film>> {
    @Override
    public Map<Integer, Film> extractData(final ResultSet rs) throws SQLException, DataAccessException {
        Map<Integer, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("film_id");
            Film film = films.get(filmId);

            if (film != null) {
                int genreId = rs.getInt("genre_id");
                if (!rs.wasNull()) {
                    film.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
                }
                continue;
            }

            film = new Film();
            film.setId(filmId);
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));

            int genreId = rs.getInt("genre_id");
            if (!rs.wasNull()) {
                film.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
            }

            films.put(filmId, film);
        }
        return films;
    }
}
