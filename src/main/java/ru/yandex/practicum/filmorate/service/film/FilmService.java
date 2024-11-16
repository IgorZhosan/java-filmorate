package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;
import java.util.Set;

public interface FilmService {
    List<Film> getFilms();

    Film getFilmById(Integer id);

    Film addFilm(Film film);

    Boolean deleteFilm(Integer id);

    Film updateFilm(Film film);

    void addUserLike(Integer filmId, Integer userId);

    void deleteUserLike(Integer filmId, Integer userId);

    List<Film> getPopularFilms(Integer count, Integer genreId, Integer year);

    List<Film> getFilmsByDirector(Integer directorId, String sortBy);

    Set<Film> getCommonFilms(Integer userId, Integer friendId);

    List<Film> searchFilm(String query, String by);
}
