package ru.yandex.practicum.filmorate.controller;

import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private final List<Film> filmList = new ArrayList<>(films.values());
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);

    @GetMapping("/get")
    public List<Film> getAllFilms() {
        if (!films.isEmpty()) {
            return List.copyOf(filmList);
        } else {
            throw new InvalidFilmException("Список фильмов пуст");
        }
    }

    @PatchMapping("/refresh")
    public Film refreshAddToFilm(@RequestBody Film film) {
        validateFilm(film);
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @PostMapping("/put")
    public Film putTheFilm(@RequestBody Film film) {
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            throw new InvalidFilmException("Название не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            throw new InvalidFilmException("Описание не может превышать 200 символов");
        }
        if (film.getReleaseDate() == null || film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new InvalidFilmException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() == null || film.getDuration() <= 0) {
            throw new InvalidFilmException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

