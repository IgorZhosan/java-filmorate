package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;

public interface FilmService {
    Collection<Film> getAllFilms();

    Film filmCreate(Film film);

    Film filmUpdate(Film film);

    Film getFilmById(int id);

    void addLike(int id, int idUser);

    void deleteLike(int id, int idUser);

    Collection<Film> getPopular(int count);

    Collection<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year);

    Collection<Film> getMostPopularFilmsByYear(int count, int year);

    Collection<Film> getMostPopularFilmsByGenre(int count, int genreId);

    void deleteFilm(final int id);
}
