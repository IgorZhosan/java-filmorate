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

    List<Film> getPopular(int count);

    List<Film> getMostPopularFilmsByGenreAndYear(int count, int genreId, int year);

    List<Film> getMostPopularFilmsByYear(int count, int year);

    List<Film> getMostPopularFilmsByGenre(int count, int genreId);

    void deleteFilm(final int id);
}
