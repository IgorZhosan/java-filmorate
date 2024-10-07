package ru.yandex.practicum.filmorate.storage.filmStorage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.DuplicateException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {

    private final Map<Long, Film> films = new HashMap<>();
    private int filmIdSequence = 1;

    @Override
    public void addingFilm(Film film) {
        if (!films.containsKey((long) film.getId())) {
            film.setId(filmIdSequence++);
            log.info("Фильм " + film + " добавлен.");
            films.put((long) film.getId(), film);
        } else {
            log.warn("Такой фильм уже есть");
            throw new DuplicateException("Такой фильм уже есть");
        }
    }

    @Override
    public void updateFilm(Film film) {
        if (films.containsKey((long) film.getId())) {
            log.info("Фильм обновлен");
            films.put((long) film.getId(), film);
        } else {
            log.warn("Нельзя обновить фильм, которого нет");
            throw new NotFoundException("Нельзя обновить фильм, которого нет");
        }
    }

    @Override
    public void deleteFilm(Long filmId) {
        if (films.containsKey(filmId)) {
            log.info("Фильм " + filmId + " удален.");
            films.remove(filmId);
        } else throw new NotFoundException("Нельзя удалить фильм, которого нет");
    }

    @Override
    public Film getFilm(Long filmId) {
        if (films.containsKey(filmId)) {
            log.info("Фильм " + filmId + " возвращен.");
            return films.get(filmId);
        } else {
            log.warn("Такого фильма нет.");
            throw new NotFoundException("Такого фильма нет");
        }
    }

    @Override
    public List<Film> getAllFilm() {
        log.info("Фильмы возвращены");
        return new ArrayList<>(films.values());
    }
}
