package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.*;
import ru.yandex.practicum.filmorate.storage.base.BaseStorage;
import ru.yandex.practicum.filmorate.storage.director.DirectorStorage;
import ru.yandex.practicum.filmorate.storage.genre.GenreStorage;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class FilmStorage extends BaseStorage<Film> implements FilmRepository {

    private final GenreStorage genreDbRepository;
    private final FilmExtractor filmExtractor;
    private final FilmGenreRowMapper filmGenreRowMapper;
    private final DirectorStorage directorDbRepository;
    private final FilmDirectorRowMapper filmDirectorRowMapper;

    public FilmStorage(NamedParameterJdbcTemplate jdbc, RowMapper<Film> mapper, GenreStorage genreDbRepository,
                       FilmExtractor filmExtractor, FilmGenreRowMapper filmGenreRowMapper, DirectorStorage directorDbRepository, FilmDirectorRowMapper filmDirectorRowMapper) {
        super(jdbc, mapper);
        this.genreDbRepository = genreDbRepository;
        this.filmExtractor = filmExtractor;
        this.filmGenreRowMapper = filmGenreRowMapper;
        this.directorDbRepository = directorDbRepository;
        this.filmDirectorRowMapper = filmDirectorRowMapper;
    }

    private static final String SQL_GET_ALL_FILMS =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id;";

    private static final String SQL_GET_FILM_BY_ID_JOIN_GENRES =
            "SELECT * FROM films AS f " +
                    "LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                    "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres as g ON g.genre_id = fg.genre_id " +
                    "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "WHERE f.film_id = :id " +
                    "ORDER BY genre_id;";

    private static final String SQL_INSERT_FILM =
            "INSERT INTO films (mpa_id, film_name, description, release_date, duration) " +
                    "VALUES (:mpa_id, :film_name, :description, :release_date, :duration);";

    private static final String SQL_INSERT_FILMS_GENRES =
            "INSERT INTO films_genres (film_id, genre_id) " +
                    "VALUES (:film_id, :genre_id);";

    private static final String SQL_INSERT_USER_FILMS_LIKES =
            "MERGE INTO users_films_likes (user_id, film_id) " +
                    "VALUES (:user_id, :film_id);";

    private static final String SQL_DELETE_FILMS_GENRES =
            "DELETE FROM films_genres WHERE film_id=:film_id";

    private static final String SQL_DELETE_FILMS_DIRECTORS =
            "DELETE FROM film_directors WHERE film_id=:film_id";

    private static final String SQL_DELETE_FILM =
            "DELETE FROM films WHERE film_id=:film_id";

    private static final String SQL_UPDATE_FILM =
            "UPDATE films SET mpa_id=:mpa_id, film_name=:film_name, description=:description, " +
                    "release_date=:release_date, duration=:duration WHERE film_id=:film_id;";

    private static final String SQL_GET_FILM_IDs_LIKE_USER =
            "SELECT film_id FROM users_films_likes WHERE user_id=:user_id;";

    private static final String SQL_GET_FILMS_BY_IDs =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id WHERE f.film_id IN (:ids);";

    private static final String SQL_DELETE_USER_FILMS_LIKES =
            "DELETE FROM users_films_likes WHERE user_id=:user_id AND film_id=:film_id;";

    private static final String SQL_GET_FILMS_BY_DIRECTOR =
            "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.mpa_name, " +
                    "fg.genre_id, g.genre_name, " +
                    "fd.director_id, d.director_name, " +
                    "COUNT(DISTINCT l.user_id) AS like_count " +
                    "FROM films AS f " +
                    "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN users_films_likes AS l ON f.film_id = l.film_id " +
                    "WHERE fd.director_id = :director_id " +
                    "GROUP BY f.film_id, fg.genre_id ";

    private static final String SQL_INSERT_FILM_DIRECTORS =
            "INSERT INTO film_directors (film_id, director_id) VALUES (:film_id, :director_id); ";

    private static final String SQL_GET_POPULAR_FILMS =
            "SELECT * FROM films AS f LEFT JOIN mpa AS r " +
                    "ON f.mpa_id = r.mpa_id " +
                    "WHERE film_id IN " +
                    "(SELECT film_id FROM USERS_FILMS_LIKES GROUP BY film_id ORDER BY COUNT(film_id) DESC) " +
                    "UNION ALL " +
                    "SELECT * FROM films AS f LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                    "WHERE film_id IN " +
                    "(SELECT film_id FROM films WHERE film_id NOT IN (SELECT film_id FROM USERS_FILMS_LIKES) ORDER BY " +
                    "film_id) LIMIT :count;";

    private static final String SQL_GET_FILMS_GENRES =
            "SELECT * FROM films_genres";

    private static final String SQL_GET_FILM_DIRECTORS =
            "SELECT * FROM film_directors";

    private static final String SQL_FILM_SEARCH =
            "SELECT f.film_id, f.film_name, f.description, f.release_date, f.duration, " +
                    "f.mpa_id, m.mpa_name, " +
                    "fg.genre_id, g.genre_name, " +
                    "fd.director_id, d.director_name, " +
                    "COUNT(DISTINCT l.user_id) AS like_count " +
                    "FROM films AS f " +
                    "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                    "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                    "LEFT JOIN mpa AS m ON f.mpa_id = m.mpa_id " +
                    "LEFT JOIN film_directors AS fd ON f.film_id = fd.film_id " +
                    "LEFT JOIN directors AS d ON fd.director_id = d.director_id " +
                    "LEFT JOIN users_films_likes AS l ON f.film_id = l.film_id ";

    @Override
    public List<Film> getAll() {
        return this.getFilms(SQL_GET_ALL_FILMS, Map.of());
    }

    @Override
    public Optional<Film> getById(Integer id) {
        Map<String, Object> params = Map.of("id", id);
        Optional<Film> film;
        try {
            Film res = jdbc.query(SQL_GET_FILM_BY_ID_JOIN_GENRES, params, filmExtractor);
            film = Optional.ofNullable(res);
        } catch (EmptyResultDataAccessException ignored) {
            film = Optional.empty();
        }
        return film;
    }

    @Override
    public Film addFilm(Film film) {
        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        jdbc.update(SQL_INSERT_FILM, params, keyHolder);
        film.setId(keyHolder.getKeyAs(Integer.class));
        addGenresToDb(film);
        addDirectorsToDb(film);
        film = getById(film.getId()).orElseThrow(() -> new NotFoundException("Ошибка при добавлении фильма"));
        return film;
    }

    @Override
    public Boolean deleteFilm(Integer id) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", id);
        jdbc.update(SQL_DELETE_FILMS_GENRES, params);
        int res = jdbc.update(SQL_DELETE_FILM, params);
        return (res == 1);
    }

    @Override
    public Film updateFilm(Film film) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", film.getId());
        params.addValue("mpa_id", film.getMpa().getId());
        params.addValue("film_name", film.getName());
        params.addValue("description", film.getDescription());
        params.addValue("release_date", film.getReleaseDate());
        params.addValue("duration", film.getDuration());
        jdbc.update(SQL_UPDATE_FILM, params);
        jdbc.update(SQL_DELETE_FILMS_GENRES, params);
        jdbc.update(SQL_DELETE_FILMS_DIRECTORS, params);
        addGenresToDb(film);
        addDirectorsToDb(film);
        return getById(film.getId()).get();
    }

    @Override
    public void addUserLike(Integer filmId, Integer userId) {
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("film_id", filmId);
        params.addValue("user_id", userId);
        jdbc.update(SQL_INSERT_USER_FILMS_LIKES, params);
    }

    @Override
    public void deleteUserLike(Integer filmId, Integer userId) {
        jdbc.update(SQL_DELETE_USER_FILMS_LIKES,
                Map.of("film_id", filmId, "user_id", userId));
    }

    @Override
    public Set<Film> getLikeFilmsByUserId(Integer userId) {
        Set<Integer> filmIds = new HashSet<>(jdbc.query(SQL_GET_FILM_IDs_LIKE_USER, Map.of("user_id", userId),
                new SingleColumnRowMapper<>(Integer.class)));
        List<Film> films = getFilms(SQL_GET_FILMS_BY_IDs, Map.of("ids", filmIds));
        return new LinkedHashSet<>(films);
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        return this.getFilms(SQL_GET_POPULAR_FILMS, Map.of("count", count));
    }

    @Override
    public List<Film> getPopularFilmsWithYear(Integer count, Integer year) {
        final String sql = "SELECT * " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                "WHERE f.film_id IN (" +
                "    SELECT film_id " +
                "    FROM USERS_FILMS_LIKES " +
                "    GROUP BY film_id " +
                "    ORDER BY COUNT(film_id) DESC " +
                ") " +
                "AND EXTRACT(YEAR FROM f.release_date) = :year " +
                "LIMIT :count;";
        return this.getFilms(sql, Map.of("count", count, "year", year));
    }

    @Override
    public List<Film> getPopularFilmsWithGenre(Integer count, Integer genreId) {
        final String sql = "SELECT * " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id IN (" +
                "    SELECT film_id " +
                "    FROM USERS_FILMS_LIKES " +
                "    GROUP BY film_id " +
                "    ORDER BY COUNT(film_id) DESC " +
                ") " +
                "AND g.genre_id = :genreId " +
                "LIMIT :count;";
        return this.getFilms(sql, Map.of("count", count, "genreId", genreId));
    }

    @Override
    public List<Film> getPopularFilmsWithGenreAndYear(Integer count, Integer year, Integer genreId) {
        final String sql = "SELECT * " +
                "FROM films AS f " +
                "LEFT JOIN mpa AS r ON f.mpa_id = r.mpa_id " +
                "LEFT JOIN films_genres AS fg ON f.film_id = fg.film_id " +
                "LEFT JOIN genres AS g ON fg.genre_id = g.genre_id " +
                "WHERE f.film_id IN (" +
                "    SELECT film_id " +
                "    FROM USERS_FILMS_LIKES " +
                "    GROUP BY film_id " +
                "    ORDER BY COUNT(film_id) DESC " +
                ")" +
                "AND g.genre_id = :genreId " +
                "AND EXTRACT(YEAR FROM f.release_date) = :year " +
                "LIMIT :count;";
        return this.getFilms(sql, Map.of("count", count, "year", year, "genreId", genreId));
    }

    @Override
    public List<Film> getFilmsByDirector(Integer directorId, String sortBy) {
        String str;
        if (sortBy.equals("year")) {
            str = SQL_GET_FILMS_BY_DIRECTOR + "ORDER BY f.release_date; ";
        } else {
            str = SQL_GET_FILMS_BY_DIRECTOR + "ORDER BY like_count DESC; ";
        }
        return this.getFilms(str, Map.of("director_id", directorId));
    }

    @Override
    public List<Film> searchFilm(String query, String by) {
        String group = "GROUP BY f.film_id, fg.genre_id, fd.director_id " +
                "ORDER BY like_count DESC; ";
        String sql;

        if ("director".equals(by)) {
            sql = "WHERE LOWER(d.director_name) LIKE LOWER(:param) ";
        } else if ("title".equals(by)) {
            sql = "WHERE LOWER(f.film_name) LIKE LOWER(:param) ";
        } else if ("director,title".equals(by) || "title,director".equals(by)) {
            sql = "WHERE (LOWER(f.film_name) LIKE LOWER(:param)) OR (LOWER(d.director_name) LIKE LOWER(:param)) ";
        } else {
            throw new IllegalArgumentException("Неверное значение параметра 'by': " + by);
        }
        String result = SQL_FILM_SEARCH + sql + group;
        String param = "%" + query + "%";

        return this.getFilms(result, Map.of("param", param));
    }

    private List<Film> getFilms(String query, Map<String, Object> map) {
        List<Film> films = jdbc.query(query, map, mapper);
        films.forEach(f -> f.setGenres(new LinkedHashSet<>()));
        films.forEach(f -> f.setDirectors(new LinkedHashSet<>()));
        LinkedHashMap<Integer, Film> filmsMap = new LinkedHashMap<>(films.stream()
                .collect(Collectors.toMap(Film::getId, Function.identity())));
        List<Genre> genres = genreDbRepository.getAll();
        LinkedHashMap<Integer, Genre> genresMap = new LinkedHashMap<>(genres.stream()
                .collect(Collectors.toMap(Genre::getId, Function.identity())));
        List<FilmGenre> relation = jdbc.query(SQL_GET_FILMS_GENRES, filmGenreRowMapper);
        relation.forEach(fg -> {
            if (filmsMap.containsKey(fg.getFilmId())) {
                filmsMap.get(fg.getFilmId()).getGenres().add(
                        genresMap.get(fg.getGenreId()));
            }
        });
        List<Director> directors = directorDbRepository.getAll();
        LinkedHashMap<Integer, Director> directorMap = new LinkedHashMap<>(directors.stream()
                .collect(Collectors.toMap(Director::getId, Function.identity())));
        List<FilmDirector> relation2 = jdbc.query(SQL_GET_FILM_DIRECTORS, filmDirectorRowMapper);
        relation2.forEach(fd -> {
            if (filmsMap.containsKey(fd.getFilmId())) {
                filmsMap.get(fd.getFilmId()).getDirectors().add(
                        directorMap.get(fd.getDirectorId()));
            }
        });
        return films;
    }

    private void addGenresToDb(Film film) {
        MapSqlParameterSource params;
        if (film.getGenres() != null) {
            List<SqlParameterSource> listParams = new ArrayList<>();
            for (Genre genre : film.getGenres()) {
                params = new MapSqlParameterSource();
                params.addValue("film_id", film.getId());
                params.addValue("genre_id", genre.getId());
                listParams.add(params);
            }
            jdbc.batchUpdate(SQL_INSERT_FILMS_GENRES, listParams
                    .toArray(new SqlParameterSource[0]));
        }
    }

    private void addDirectorsToDb(Film film) {
        MapSqlParameterSource params;
        if (film.getDirectors() != null) {
            List<SqlParameterSource> listParams = new ArrayList<>();
            for (Director director : film.getDirectors()) {
                params = new MapSqlParameterSource();
                params.addValue("film_id", film.getId());
                params.addValue("director_id", director.getId());
                listParams.add(params);
            }
            jdbc.batchUpdate(SQL_INSERT_FILM_DIRECTORS, listParams
                    .toArray(new SqlParameterSource[0]));
        }
    }
}