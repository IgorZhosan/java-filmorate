package ru.yandex.practicum.filmorate.storage.genre;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

public interface GenreStorage {
    Optional<Genre> getGenreById(int id);

    List<Genre> getAllGenres();

    List<Genre> getGenresList(List<Integer> ids);
}