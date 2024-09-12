package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.InvalidFilmException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.HashMap;
import java.util.Map;

@Validated
@RestController
@RequestMapping("/home")
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();

    @GetMapping("/films")
    public Map<Integer, Film> getAllFilms() {
        if (!films.isEmpty()) {
            return films;
        } else {
            throw new InvalidFilmException("Список пользователей пуст");
        }
    }

    @PatchMapping("/refresh")
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        if (films.containsKey(film.getId())) {
            films.put(film.getId(), film);
        }
        return film;
    }

    @PostMapping("/put")
    public Film putTheFilm(@Valid @RequestBody Film film) throws Exception {
        films.put(film.getId(), film);
        return film;
    }
}