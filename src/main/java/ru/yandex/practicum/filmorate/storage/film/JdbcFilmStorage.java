package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.film.extractor.FilmsExtractor;

import java.util.*;

@Repository
@RequiredArgsConstructor
public class JdbcFilmStorage implements FilmStorage {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcOperations jdbc;
    private final FilmExtractor filmExtractor;
    private final FilmsExtractor filmsExtractor;

    @Override   //получение списка фильмов
    public Collection<Film> getAllFilms() {
        String sql = "SELECT * " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id; ";
        Map<Integer, Film> films = jdbc.query(sql, Map.of(), filmsExtractor);
        assert films != null;

        return films.values().stream().toList();
    }

    @Override // для добавления нового фильма в список.
    public Film filmCreate(final Film film) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO FILMS (name, description, release_date, duration, mpa_id) " +
                "VALUES (:name, :description, :release_date, :duration, :mpa_id); ";
        Map<String, Object> params = new HashMap<>();

        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpa_id", film.getMpa().getId());

        jdbc.update(sql, new MapSqlParameterSource().addValues(params), keyHolder, new String[]{"film_id"});
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        addGenres(film.getId(), film.getGenres());

        return film;
    }

    @Override //для обновления данных существующего фильма.
    public Film filmUpdate(final Film film) {
        String sql = "UPDATE films SET name = :name, " +
                "description = :description, " +
                "release_date = :release_date, " +
                "duration = :duration, " +
                "mpa_id = :mpa_id " +
                "WHERE film_id = :film_id; ";
        Map<String, Object> params = new HashMap<>();

        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpa_id", film.getMpa().getId());
        params.put("film_id", film.getId());

        jdbc.update(sql, params);
        addGenres(film.getId(), film.getGenres());

        return film;
    }

    @Override //получение фильма по id
    public Optional<Film> getFilmById(final int id) {
        String sql = "SELECT * " +
                "FROM films f " +
                "JOIN mpa m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_genres fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id = :film_id; ";
        Film film = jdbc.query(sql, Map.of("film_id", id), filmExtractor);

        return Optional.ofNullable(film);
    }

    @Override //добавление лайка
    public void addLike(final int id, final int userId) {
        String sql = "MERGE INTO likes(film_id, user_id) VALUES (:film_id, :user_id); ";
        jdbc.update(sql, Map.of("film_id", id, "user_id", userId));
    }

    @Override // удаление лайка
    public void deleteLike(final int id, final int userId) {
        String sql = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id; ";
        jdbc.update(sql, Map.of("film_id", id, "user_id", userId));
    }

    @Override
    public List<Film> getPopular(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "GROUP BY f.film_id " +
                "ORDER BY like_count DESC " +
                "LIMIT :count;";


        Map<Integer, Film> films = jdbc.query(sql, Map.of("count", count), filmsExtractor);
        assert films != null;

        return films.values().stream().toList();
    }

    @Override
    public List<Integer> getAllId() {
        return jdbcTemplate.query("SELECT film_id FROM films; ", (rs, rowNum) -> rs.getInt("film_id"));
    }

    private void addGenres(final int filmId, final Set<Genre> genres) {
        Map<String, Object>[] batch = new HashMap[genres.size()];
        int count = 0;

        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batch[count++] = map;
        }

        String sqlDelete = "DELETE FROM film_genres WHERE film_id = :film_id AND genre_id = :genre_id; ";
        String sqlInsert = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id); ";
        jdbc.batchUpdate(sqlDelete, batch);
        jdbc.batchUpdate(sqlInsert, batch);
    }

    @Override
    public void deleteFilm(final int filmId) {
        // Удаляем все записи из film_genres, связанные с данным фильмом
        String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = :film_id";
        jdbc.update(deleteGenresSql, Map.of("film_id", filmId));

        // Удаляем все лайки, связанные с данным фильмом
        String deleteLikesSql = "DELETE FROM likes WHERE film_id = :film_id";
        jdbc.update(deleteLikesSql, Map.of("film_id", filmId));

        // Удаляем сам фильм из таблицы films
        String deleteFilmSql = "DELETE FROM films WHERE film_id = :film_id";
        jdbc.update(deleteFilmSql, Map.of("film_id", filmId));
    }

    @Override
    public void deleteGenresByFilmId(final int filmId) {
        String sql = "DELETE FROM film_genres WHERE film_id = :film_id";
        jdbc.update(sql, Map.of("film_id", filmId));
    }

    @Override
    public void deleteLikesByFilmId(final int filmId) {
        String sql = "DELETE FROM likes WHERE film_id = :film_id";
        jdbc.update(sql, Map.of("film_id", filmId));
    }
}
