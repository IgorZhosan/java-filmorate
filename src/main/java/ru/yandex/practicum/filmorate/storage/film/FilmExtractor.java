package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedHashSet;

@Component
public class FilmExtractor implements ResultSetExtractor<Film> {

    @Override
    public Film extractData(ResultSet rs)
            throws SQLException, DataAccessException {
        Film film = null;

        while (rs.next()) {

            // Данные о фильме - заполняем объект только один раз
            if (film == null) {
                film = Film.builder()
                        .id(rs.getInt("film_id"))
                        .name(rs.getString("film_name"))
                        .description(rs.getString("description"))
                        .releaseDate(rs.getDate("release_date").toLocalDate())
                        .duration(rs.getInt("duration"))
                        .build();

                film.setMpa(Mpa.builder().id(rs.getInt("mpa_id"))
                        .name(rs.getString("mpa_name"))
                        .build());

                film.setGenres(new LinkedHashSet<>());
                film.setDirectors(new LinkedHashSet<>());
            }

            // Создаем объект жанра, если по нему есть данные
            Genre genre = Genre.builder().build();
            Integer genreId = rs.getInt("genre_id");

            if (!rs.wasNull()) {
                genre.setId(genreId);
                genre.setName(rs.getString("genre_name"));
                film.getGenres().add(genre);
            }

            // Создаем объект режиссера, если по нему есть данные
            Director director = Director.builder().build();
            Integer directorId = rs.getInt("director_id");

            if (!rs.wasNull()) {
                director.setId(directorId);
                director.setName(rs.getString("director_name"));
                film.getDirectors().add(director);
            }
        }
        return film;
    }
}