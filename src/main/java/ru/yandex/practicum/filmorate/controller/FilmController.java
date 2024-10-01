package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/films")
@Validated
public class FilmController {

    private final Map<Integer, Film> films = new HashMap<>();
    private static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private int filmIdSequence = 1;

    @GetMapping
    public List<Film> getAllFilms() {
        if (films.isEmpty()) {
            throw new ValidationException("Список фильмов пуст");
        }
        return new ArrayList<>(films.values());
    }

    @PutMapping
    public Film refreshAddToFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            throw new ValidationException("Фильм с таким ID не найден");
        }
        validateFilm(film);
        films.put(film.getId(), film);
        return film;
    }

    @PostMapping
    public Film addFilm(@Valid @RequestBody Film film) {
        if (!films.containsKey(film)) {
            validateFilm(film);
            film.setId(filmIdSequence++);
            films.put(film.getId(), film);
            return film;
        } else throw new ValidationException("Такой фильм уже есть");
    }

    private void validateFilm(Film film) {
        if (film.getReleaseDate() == null && film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
    }
}
