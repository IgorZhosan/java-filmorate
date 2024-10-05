package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private int filmIdSequence = 1;

    @Override
    public void addingFilm(Film film) {
        if (!films.containsKey((long) film.getId())) {
            film.setId(filmIdSequence++);
            films.put((long) film.getId(), film);
        } else throw new ValidationException("Такой фильм уже есть");
    }

    @Override
    public void updateFilm(Film film) {
        if (films.containsKey((long) film.getId())) {
            films.put((long) film.getId(), film);
        } else throw new ValidationException("Нельзя обновить фильм, которого нет");
    }

    @Override
    public void deleteFilm(Long filmId) {
        if (films.containsKey(filmId)) {
            films.remove(filmId);
        } else throw new ValidationException("Нельзя удалить фильм, которого нет");
    }

    @Override
    public Film getFilm(Long filmId) {
        if (films.containsKey(filmId)) {
            return films.get(filmId);
        } else throw new ValidationException("Такого фильма нет");
    }

    @Override
    public List<Film> getAllFilm() {
        return new ArrayList<>(films.values());
    }
}
