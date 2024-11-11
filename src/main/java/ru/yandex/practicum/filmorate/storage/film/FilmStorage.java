package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

public interface FilmStorage {
    Collection<Film> getAllFilms(); // получение списка всех фильмов

    Film filmCreate(Film film); // добавление фильма

    Film filmUpdate(Film film); //обновление фильма

    Optional<Film> getFilmById(int id); //получение фильма по id

    void addLike(int id, int userId); //добавление лайка

    void deleteLike(int id, int userId); // удаление лайка

    Collection<Film> getPopular(int count); // получение списка лучших фильмов

     List<Integer> getAllId();

    void deleteFilm(final int filmId); //удаление фильма по id

    void deleteGenresByFilmId(int filmId);

    void deleteLikesByFilmId(int filmId);

    Collection<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year); // получение списка лучших фильмов по жанру и году

    Collection<Film> getMostPopularFilmsByYear(int count, int year); // получение списка лучших фильмов по году

    Collection<Film> getMostPopularFilmsByGenre(int count, int genreId); // получение списка лучших фильмов по году
}


