package ru.yandex.practicum.filmorate.service;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);

    List<Film> popularFilmsBasedOnLiked(int count);

    void addingFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Long filmId);

    void validateFilm(Film film);

    Film getFilm(Long filmId);

    List<Film> getAllFilm();
}
