package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface FilmRepository {
    List<Film> getAll();

    Optional<Film> getById(Integer id);

    Film addFilm(Film film);

    Boolean deleteFilm(Integer id);

    Film updateFilm(Film film);

    void addUserLike(Integer filmId, Integer userId);

    void deleteUserLike(Integer filmId, Integer userId);

    Set<Film> getLikeFilmsByUserId(Integer userId);

    List<Film> getPopularFilmsWithGenreAndYear(Integer count, Integer year, Integer genreId);

    List<Film> getPopularFilmsWithGenre(Integer count, Integer genreId);

    List<Film> getPopularFilmsWithYear(Integer count, Integer year);

    List<Film> getPopularFilms(Integer count);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    List<Film> searchFilm(String query, String by);
}