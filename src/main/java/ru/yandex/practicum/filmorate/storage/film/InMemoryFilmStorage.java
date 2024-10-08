package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.*;

@Component
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long currentId = 0;

    @Override   //получение списка фильмов
    public Collection<Film> getAllFilms() {
        return List.copyOf(films.values());
    }

    @Override // для добавления нового фильма в список.
    public Film filmCreate(Film film) {
        film.setId(getIdNext());
        films.put(film.getId(), film);
        return film;
    }

    @Override //для обновления данных существующего фильма.
    public Film filmUpdate(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public Map<Long, Film> getFilms() {
        return films;
    }

    private long getIdNext() {
        return ++currentId;
    }
}
