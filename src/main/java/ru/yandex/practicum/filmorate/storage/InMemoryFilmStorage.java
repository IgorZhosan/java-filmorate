package ru.yandex.practicum.filmorate.storage;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Integer, Film> films = new HashMap<>();
    private int filmIdSequence = 1;

    @Override
    public void addingFilm(Film film) {
        if (!films.containsKey(film.getId())) {
            film.setId(++filmIdSequence);
            films.put(film.getId(), film);
        } else throw new ValidationException("Такой фильм уже есть");
    }

    @Override
    public void updateFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        } else throw new ValidationException("Нельзя обновить фильм, которого нет");
    }

    @Override
    public void deleteFilm(Film film) {
        if (films.containsKey(film.getId())) {
            films.remove(film.getId());
        } else throw new ValidationException("Нельзя удалить фильм, которого нет");
    }

    @Override
    public Film getFilm(Film film) {
        if (films.containsKey(film.getId())) {
            return films.get(film.getId());
        } else throw new ValidationException("Такого фильма нет");
    }
}
