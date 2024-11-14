package ru.yandex.practicum.filmorate.storage.film.extractor;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(final ResultSet rs) throws SQLException, DataAccessException {
        Film film = null;

        if (rs.next()) {
            film = new Film();
            film.setName(rs.getString("name"));
            film.setDescription(rs.getString("description"));
            film.setReleaseDate(rs.getDate("release_date").toLocalDate());
            film.setDuration(rs.getInt("duration"));
            film.setId(rs.getInt("film_id"));
            film.setMpa(new Mpa(rs.getInt("mpa_id"), rs.getString("mpa_name")));

            int idGenre = rs.getInt("genre_id");
            if (idGenre != 0) {
                do {
                    film.getGenres()
                            .add(new Genre(rs.getInt("genre_id"), rs.getString("genre_name")));
                } while (rs.next());
            }
        }
        return film;
    }
}