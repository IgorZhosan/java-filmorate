package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.filmorate.model.Director;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.enums.EventType;
import ru.yandex.practicum.filmorate.model.enums.Operation;
import ru.yandex.practicum.filmorate.storage.feed.FeedStorage;
import ru.yandex.practicum.filmorate.storage.film.extractor.FilmExtractor;
import ru.yandex.practicum.filmorate.storage.film.extractor.FilmsExtractor;

import java.util.*;

@Repository
@RequiredArgsConstructor
@Slf4j
public class JdbcFilmStorage implements FilmStorage {
    @Autowired
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcOperations jdbc;
    private final FilmExtractor filmExtractor;
    private final FilmsExtractor filmsExtractor;
    private final FeedStorage feedStorage;

    @Override
    public Collection<Film> getAllFilms() {
        log.info("Получение списка всех фильмов.");
        String sql = """
                SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                       f.mpa_id, m.mpa_name,
                       g.genre_id, g.genre_name,
                       d.director_id, d.name AS director_name
                FROM films f
                LEFT JOIN mpa m ON f.mpa_id = m.mpa_id
                LEFT JOIN film_genres fg ON f.film_id = fg.film_id
                LEFT JOIN genres g ON fg.genre_id = g.genre_id
                LEFT JOIN film_directors fd ON f.film_id = fd.film_id
                LEFT JOIN directors d ON fd.director_id = d.director_id;
                """;
        Map<Integer, Film> films = jdbc.query(sql, Map.of(), filmsExtractor);
        if (films == null || films.isEmpty()) {
            log.warn("Фильмы не найдены или результат пуст");
            return Collections.emptyList();
        }

        log.info("Количество фильмов, полученных из БД: {}", films.size());
        return new ArrayList<>(films.values());
    }

    @Override // для добавления нового фильма в список.
    @Transactional
    public Film filmCreate(final Film film) {
        log.info("Добавление фильма: {}", film);
        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO FILMS (name, description, release_date, duration, mpa_id) " +
                    "VALUES (:name, :description, :release_date, :duration, :mpa_id);";
        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpa_id", film.getMpa().getId());

        jdbc.update(sql, new MapSqlParameterSource().addValues(params), keyHolder, new String[]{"film_id"});
        film.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());

        addGenres(film.getId(), film.getGenres());
        addDirectors(film.getId(), film.getDirectors());

        log.info("Фильм после добавления жанров и режиссёров: {}", film);
        return film;
    }

    @Override //для обновления данных существующего фильма.
    public Film filmUpdate(Film film) {
        log.info("Обновление фильма с id {}", film.getId());
        log.info("Данные фильма перед обновлением: {}", film);

        String sql = "UPDATE films SET name = :name, " +
                "description = :description, " +
                "release_date = :release_date, " +
                "duration = :duration, " +
                "mpa_id = :mpa_id " +
                "WHERE film_id = :film_id;";

        Map<String, Object> params = new HashMap<>();
        params.put("name", film.getName());
        params.put("description", film.getDescription());
        params.put("release_date", film.getReleaseDate());
        params.put("duration", film.getDuration());
        params.put("mpa_id", film.getMpa() != null ? film.getMpa().getId() : null);
        params.put("film_id", film.getId());

        log.info("Параметры запроса на обновление: {}", params);

        jdbc.update(sql, params);
        addGenres(film.getId(), film.getGenres());
        addDirectors(film.getId(), film.getDirectors());

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
        feedStorage.makeEvent(userId, id, EventType.LIKE, Operation.ADD);
    }

    @Override // удаление лайка
    public void deleteLike(final int id, final int userId) {
        String sql = "DELETE FROM likes WHERE film_id = :film_id AND user_id = :user_id; ";
        jdbc.update(sql, Map.of("film_id", id, "user_id", userId));
        feedStorage.makeEvent(userId, id, EventType.LIKE, Operation.REMOVE);
    }

    public Collection<Film> getPopular(int count) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, " +
                "g.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE f.film_id IS NOT NULL " +
                "GROUP BY f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, d.director_id, d.name, g.genre_id, g.genre_name " +
                "ORDER BY like_count DESC " +
                "LIMIT " + count;

        Map<Integer, Film> filmsMap = jdbcTemplate.query(sql, filmsExtractor);

        if (filmsMap == null || filmsMap.isEmpty()) {
            log.info("Популярные фильмы не найдены или список пуст.");
            return new ArrayList<>();
        }

        log.info("Количество популярных фильмов: {}", filmsMap.size());

        return new ArrayList<>(filmsMap.values());
    }

    @Override // получение списка лучших фильмов по жанру и году
    public Collection<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, " +
                "fg.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE g.genre_id = :genreId AND YEAR(f.release_date) = :year " +
                "GROUP BY f.film_id, fg.genre_id " + "ORDER BY like_count DESC " +
                "LIMIT :count;";

        Map<Integer, Film> films = jdbc.query(sql,
                Map.of("genreId", genreId, "year", year, "count", count), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы не найдены или список пуст.");
            return new ArrayList<>();
        }
        return films.values();
    }

    @Override // получение списка лучших фильмов по году
    public Collection<Film> getMostPopularFilmsByYear(int count, int year) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, " +
                "fg.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE YEAR(f.release_date) = :year " + "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY like_count DESC " + "LIMIT :count;";
        Map<Integer, Film> films = jdbc.query(sql, Map.of("year", year, "count", count), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы не найдены или список пуст.");
            return new ArrayList<>();
        }
        return films.values();
    }

    @Override // получение списка лучших фильмов по жанру
    public Collection<Film> getMostPopularFilmsByGenre(int count, int genreId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, " +
                "fg.genre_id, g.genre_name " + "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE g.genre_id = :genreId " + "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY like_count DESC " + "LIMIT :count;";
        Map<Integer, Film> films = jdbc.query(sql, Map.of("genreId", genreId, "count", count), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы не найдены или список пуст.");
            return new ArrayList<>();
        }
        return films.values();
    }

    @Override
    public List<Integer> getAllId() {
        return jdbcTemplate.query("SELECT film_id FROM films; ", (rs, rowNum) -> rs.getInt("film_id"));
    }

    private void addGenres(final int filmId, final Set<Genre> genres) {
        // Удаляем все жанры для фильма
        String sqlDelete = "DELETE FROM film_genres WHERE film_id = :film_id";
        jdbc.update(sqlDelete, Map.of("film_id", filmId));

        // Если коллекция жанров пуста или равна null, завершаем метод
        if (genres == null || genres.isEmpty()) {
            return;
        }

        // Подготовка данных для вставки новых жанров
        Map<String, Object>[] batch = new Map[genres.size()];
        int count = 0;

        for (Genre genre : genres) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("genre_id", genre.getId());
            batch[count++] = map;
        }

        // Вставляем новые жанры
        String sqlInsert = "INSERT INTO film_genres (film_id, genre_id) VALUES (:film_id, :genre_id)";
        jdbc.batchUpdate(sqlInsert, batch);
    }

    @Override
    @Transactional
    public void deleteFilm(final int filmId) {
        // Удаляем все записи из film_genres, связанные с данным фильмом
        String deleteGenresSql = "DELETE FROM film_genres WHERE film_id = :film_id";
        jdbc.update(deleteGenresSql, Map.of("film_id", filmId));

        // Удаляем всех режиссёров, связанных с фильмом
        String deleteDirectorsSql = "DELETE FROM film_directors WHERE film_id = :film_id";
        jdbc.update(deleteDirectorsSql, Map.of("film_id", filmId));

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

    @Override
    public List<Film> getFilmsByDirector(int directorId) {
        String sql = """
                    SELECT f.film_id, f.name, f.description, f.release_date, f.duration,
                           f.mpa_id, m.mpa_name,
                           g.genre_id, g.genre_name,
                           d.director_id, d.name AS director_name
                    FROM films AS f
                    LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id
                    LEFT JOIN genres AS g ON fg.genre_id = g.genre_id
                    LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id
                    LEFT JOIN directors AS d ON fd.director_id = d.director_id
                    LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id
                    WHERE fd.director_id = :director_id
                """;

        Map<Integer, Film> films = jdbc.query(sql, Map.of("director_id", directorId), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.warn("Фильмы с directorId={} не найдены", directorId);
            return Collections.emptyList();
        }

        return new ArrayList<>(films.values());
    }

    private void addDirectors(final int filmId, final Set<Director> directors) {
        // Удаляем всех режиссёров для фильма
        String sqlDelete = "DELETE FROM film_directors WHERE film_id = :film_id";
        jdbc.update(sqlDelete, Map.of("film_id", filmId));

        // Если коллекция режиссёров пуста или равна null, завершаем метод
        if (directors == null || directors.isEmpty()) {
            return;
        }

        // Подготовка данных для вставки новых режиссёров
        Map<String, Object>[] batch = new Map[directors.size()];
        int count = 0;

        for (Director director : directors) {
            Map<String, Object> map = new HashMap<>();
            map.put("film_id", filmId);
            map.put("director_id", director.getId());
            batch[count++] = map;
        }

        // Вставляем новых режиссёров
        String sqlInsert = "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id)";
        jdbc.batchUpdate(sqlInsert, batch);
    }

    @Override
    public Collection<Film> getCommonFilms(int userId, int friendId) {
        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(fl.user_id) AS like_count, " +
                "g.genre_id, g.genre_name, " + "d.director_id, d.name AS director_name " +
                "FROM films AS f " + "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "LEFT JOIN likes AS fl ON f.film_id = fl.film_id " +
                "WHERE f.film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "AND f.film_id IN (SELECT film_id FROM likes WHERE user_id = ?) " +
                "GROUP BY f.film_id " + "ORDER BY like_count DESC";

        Map<Integer, Film> films = jdbcTemplate.query(sql, new FilmsExtractor(), userId, friendId);

        return films == null || films.isEmpty() ? Collections.emptyList() : new ArrayList<>(films.values());
    }

    @Override
    public Collection<Film> getFilmsByTitle(String query) {

        String correctQuery = "%" + query + "%";

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, fg.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE LOWER(f.name) LIKE LOWER(:correctQuery) " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY like_count DESC ";

        Map<Integer, Film> films = jdbc.query(sql, Map.of("correctQuery", correctQuery), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы c поиском по такому названию не найдены или список пуст.");
            return Collections.emptyList();
        }
        return films.values();
    }

    @Override
    public Collection<Film> getFilmsByDirectorName(String query) {

        String correctQuery = "%" + query + "%";

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, fg.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE LOWER(d.name) LIKE LOWER(:correctQuery) " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY like_count DESC ";

        Map<Integer, Film> films = jdbc.query(sql, Map.of("correctQuery", correctQuery), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы c поиском по такому режиссеру не найдены или список пуст.");
            return Collections.emptyList();
        }
        return films.values();
    }

    @Override
    public Collection<Film> getFilmsByDirectorAndTitle(String query) {

        String correctQuery = "%" + query + "%";

        String sql = "SELECT f.film_id, f.name, f.description, f.release_date, f.duration, " +
                "f.mpa_id, m.mpa_name, COUNT(DISTINCT l.user_id) AS like_count, " +
                "d.director_id, d.name AS director_name, fg.genre_id, g.genre_name " +
                "FROM films AS f " +
                "LEFT JOIN film_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                "LEFT JOIN likes AS l ON f.film_id = l.film_id " +
                "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                "WHERE LOWER(d.name) LIKE LOWER(:correctQuery) OR LOWER(f.name) LIKE LOWER(:correctQuery) " +
                "GROUP BY f.film_id, fg.genre_id " +
                "ORDER BY like_count DESC ";

        Map<Integer, Film> films = jdbc.query(sql, Map.of("correctQuery", correctQuery), filmsExtractor);

        if (films == null || films.isEmpty()) {
            log.info("Популярные фильмы c поиском по такому режиссеру или названию не найдены или список пуст.");
            return Collections.emptyList();
        }
        return films.values();
    }
}