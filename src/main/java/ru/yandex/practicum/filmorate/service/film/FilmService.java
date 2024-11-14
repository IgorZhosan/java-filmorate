package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.enums.SearchType;

import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    List<Film> getFilmsByDirectorSorted(int directorId, String sortBy);

    Collection<Film> getCommonFilms(int userId, int friendId);

    Collection<Film> getSearchedFilms(String query, Set<SearchType> by);
}