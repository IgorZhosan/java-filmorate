package ru.yandex.practicum.filmorate.storage;

import ru.yandex.practicum.filmorate.model.Film;

public interface FilmStorage {
    void addingFilm(Film film);

    void refreshFilm(Film film);

    void deleteFilm(Film film);
}
