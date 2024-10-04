package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {

    void addingFilm(Film film);

    void updateFilm(Film film);

    void deleteFilm(Long filmId);

    Film getFilm(Long filmId);

    List<Film> getAllFilm();
}
