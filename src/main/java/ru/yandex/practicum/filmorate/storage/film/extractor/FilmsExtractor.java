package ru.yandex.practicum.filmorate.storage.film.extractor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;

@Component
@Slf4j
public class FilmsExtractor implements ResultSetExtractor<Map<Integer, Film>> {

    @Override
    public Map<Integer, Film> extractData(ResultSet rs) throws SQLException {
        Map<Integer, Film> films = new LinkedHashMap<>();

        while (rs.next()) {
            int filmId = rs.getInt("film_id");
            Film film = films.computeIfAbsent(filmId, id -> {
                try {
                    Film newFilm = new Film();
                    newFilm.setId(filmId);
                    newFilm.setName(rs.getString("name"));
                    newFilm.setDescription(rs.getString("description"));
                    newFilm.setReleaseDate(rs.getDate("release_date").toLocalDate());
                    newFilm.setDuration(rs.getInt("duration"));
                    newFilm.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));
                    newFilm.setGenres(new LinkedHashSet<>());
                    newFilm.setDirectors(new LinkedHashSet<>());
                    return newFilm;
                } catch (SQLException e) {
                    throw new RuntimeException("Error creating film instance", e);
                }
            });

            int genreId = rs.getInt("genre_id");
            if (genreId != 0) {
                film.getGenres().add(new Genre(genreId, rs.getString("genre_name")));
            }

            int directorId = rs.getInt("director_id");
            if (directorId != 0) {
                film.getDirectors().add(new Director(directorId, rs.getString("director_name")));
            }
        }

        return films;
    }
}
