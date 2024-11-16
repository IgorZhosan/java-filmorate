package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface GenreRepository {
    List<Genre> getAll();

    Optional<Genre> getById(Integer id);

    Set<Genre> getGenresByFilmId(Integer filmId);

    Set<Integer> getGenresIdByFilmId(Integer filmId);
}