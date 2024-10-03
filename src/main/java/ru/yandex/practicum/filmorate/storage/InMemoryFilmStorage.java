package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();

    @Override
    public void addingFilm(Film film) {

    }

    @Override
    public void refreshFilm(Film film) {

    }

    @Override
    public void deleteFilm(Film film) {

    }
}
